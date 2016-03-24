package lambda

import scala.collection.mutable
import scala.io.Source

/**
 * Created by zhoufeng on 16/3/21.
 */
class InputStream(val input: Source) {
  private var lineNo = 1
  private var column = 0
  private val queue = mutable.Queue[Char]()

  def next(): Option[Char] = {
    val nextChar = if(queue.isEmpty) {
      if(input.isEmpty) None else Some(input.next())
    } else Some(queue.dequeue())

    nextChar match {
      case Some("\n") => {lineNo += 1; column = 0}
      case Some(char) => column += 1
      case _ => _
    }
    nextChar
  }

  def peek(): Option[Char] = {
    if(queue.isEmpty) {
      if(input.isEmpty) None else {queue.enqueue(input.next()); Some(queue.head)}
    } else Some(queue.head)
  }

  def croak(message: String) = throw new Exception(s"${message} (${lineNo}: ${column})")

}
