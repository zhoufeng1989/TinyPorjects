package lambda

/**
 * Created by zhoufeng on 16/3/21.
 */
class Lexer(val stream: InputStream) {
  private val keywords = List("if", "then", "else", "lambda", "λ", "true", "false")
  private val punctuation = List(',', ';', '(', ')', '{', '}', '[', ']')
  private val whitespaces = List(' ', '\n', '\t')
  private var current:String = _

  private def isWhitespace(char: Char): Boolean = whitespaces contains char
  private def isNumber(char: Char): Boolean = ('0' <= char) && (char <= '9')

  private def skip_comment():Unit = stream.next match {
    case "\n" => _
    case _ => skip_comment()
  }

  private def read_while(predicate: Char => Boolean): String = stream.peek() match {
    case char if predicate(char) => {stream.next(); char::readWhile(predicate)}
  }

  private def read_number(): Token = {
    stream.peek() match {
      case char if isNumber(char) =>
    }
  }


  def read_next(): Token = stream.peek match {
    case char if isWhitespace(char) => {stream.next(); read_next()}
    case '#' => {skip_comment(); read_next()}
    case char if isNumber(char) => read_number()
  }
}
abstract class Token {
  val value: Any

}


case class NumToken(value: Numeric) extends Token


case class StrToken(value: String) extends Token


case class VarToken(value: String) extends Token


case class KeywordToken(value: String) extends Token


case class PuncToken(value: String) extends Token


case class OpToken(value: String) extends Token


case object EofToken extends Token {
  val value = ""
}
