import gevent
import gevent.monkey
gevent.monkey.patch_all()
import urllib2
import urllib
import re
import os
import logging
from gevent.queue import Queue, Empty

index_queue = Queue()
resource_queue = Queue()

index_url = 'http://www.eslpod.com/website/show_all.php'
resource_pattern = re.compile(
    r'http://libsyn.com/media/eslpod/(?:ESLPod|EC)\d+.mp3'
)


class TimeoutException(Exception):
    pass


def retry(times):
    def _retry(target):
        def __retry(*args, **kwargs):
            count = 1
            while count <= times:
                try:
                    result = target(*args, **kwargs)
                except Exception as err:
                    logging.info('%s get error %s, already try %d times' %
                                 (target.__name__, str(err), count))
                    count += 1
                else:
                    return result
            if count > times:
                logging.error('retry more than %d times' % times)
        return __retry
    return _retry


@retry(times=3)
def fetch_index(thread_id):
    while True:
        try:
            params = index_queue.get(timeout=120)
        except Empty:
            break
        logging.info('get_index_info low_rec=%d thread_id=%d' %
                     (params['low_rec'], thread_id))
        url = index_url + '?' + urllib.urlencode(params)
        try:
            with gevent.Timeout(120, TimeoutException):
                response = urllib2.urlopen(url)
        except Exception as e:
            index_queue.put(params)
            raise e
        content = response.read()
        resources = resource_pattern.findall(content)
        map(resource_queue.put, resources)


@retry(times=3)
def download(thread_id, base_dir='~'):
    while True:
        try:
            url = resource_queue.get(timeout=120)
        except Empty:
            break
        logging.info('get_resource_url url=%s, thread_id=%d' %
                     (url, thread_id))
        try:
            with gevent.Timeout(120, TimeoutException):
                response = urllib2.urlopen(url)
        except Exception as e:
            resource_queue.put(url)
            raise e
        file_name = url.rsplit('/', 1)[1]
        with open(os.path.join(base_dir, file_name), 'w') as f:
            f.write(response.read())
            logging.info('download_resource filename=%s' % file_name)


if __name__ == '__main__':
    start = 1
    stop = 3
    thread_count = 10
    threads = []
    base_dir = '/home/zhoufeng/download_eslpod'
    logging.basicConfig(level=logging.DEBUG)
    for page in xrange(start - 1, stop):
        params = {'cat_id': -59456, 'low_rec': page * 20}
        index_queue.put(params)
    for thread in xrange(thread_count):
        threads.append(gevent.spawn(fetch_index, thread_id=thread))
    for thread in xrange(thread_count):
        threads.append(
            gevent.spawn(download, base_dir=base_dir, thread_id=thread))
    gevent.joinall(threads)
