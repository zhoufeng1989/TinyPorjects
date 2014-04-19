download_eslpods
================

download eslpod.com podcasts from [index pages](http://www.eslpod.com/website/show_all.php) based on [gevent](http://www.gevent.org/).

execute ```python download.py -h```

```
optional arguments:
  -h, --help            show this help message and exit
  -s START, --start START
                        start index page, start from 1, default 1
  -e END, --end END     end index page, default 2
  -tc THREADS_COUNT, --threads_count THREADS_COUNT
                        download thread count, default 10
  -b BASE_DIR, --base_dir BASE_DIR
                        base dir to dowload, default eslpods dir within
                        current dir
```
