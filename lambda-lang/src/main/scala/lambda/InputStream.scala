package lambda

import scala.collection.mutable
import scala.io.Source

/**
 * Created by zhoufeng on 16/3/21.
 */
class InputStream(val input: Source) {
  var lineNo = 1
  var column = 0
  val queue = mutable.Queue[Char]()

  def next(): Char = {
    val nextChar = if(queue.isEmpty) input.next() else queue.dequeue()
    nextChar match {
      case "\n" => {lineNo += 1; column = 0}
      case _ => {column += 1}
    }
    nextChar
  }

  def peek(): Char = {
    val nextChar = input.next()
    queue.enqueue(nextChar)
    nextChar
  }

  def eof: Boolean = input.isEmpty

  def croak(message: String) = throw new Exception(s"${message} (${lineNo}: ${column})")

}
