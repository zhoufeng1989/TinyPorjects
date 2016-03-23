package lambda

import scala.collection.mutable
import scala.io.Source
import scala.util.{Failure, Try, Success}

/**
 * Created by zhoufeng on 16/3/21.
 */
class InputStream(val input: Source) {
  import InputStream.eof
  var lineNo = 1
  var column = 0
  val queue = mutable.Queue[Char]()

  def next(): Try[Char] = {
    val nextChar = if(queue.isEmpty) Try(input.next()) else Success(queue.dequeue())
    nextChar match {
      case Success("\n") => {lineNo += 1; column = 0; Success('\n')}
      case Success(char) => {column += 1; Success(char)}
      case failure: Failure => failure
    }
  }

  def peek(): Char = {
    val nextChar = input.next()
    queue.enqueue(nextChar)
    nextChar
  }

  def isEof: Boolean = input.isEmpty

  def croak(message: String) = throw new Exception(s"${message} (${lineNo}: ${column})")

}

object InputStream {
  case object eof
}
