package lambda

/**
 * Created by zhoufeng on 16/3/21.
 */
class Lexer(val stream: InputStream) {
  private val keywords = List("if", "then", "else", "lambda", "λ", "true", "false")
  private val punctuation = List(',', ';', '(', ')', '{', '}', '[', ']')
  private val whitespaces = List(' ', '\n', '\t')
  private val operatorChars = List('+', '-', '*', '/', '%', '=', '&', '|', '<', '>', '!')
  private var current:String = _

  private def isWhitespace(char: Char): Boolean = whitespaces contains char
  private def isNumber(char: Char): Boolean = ('0' <= char) && (char <= '9')
  private def isOperatorChar(char: Char): Boolean = operatorChars contains char

  private def skipComment():Unit = stream.next match {
    case "\n" => _
    case _ => skipComment()
  }

  private def readWhile(predicate: Char => Boolean): String = stream.peek() match {
    case eof
    case char if predicate(char) => {stream.next(); char + readWhile(predicate)}
  }

  private def readNumber(): Token = {
    stream.peek() match {
      case char if isNumber(char) =>
    }
  }

  private def readString(): Token = {
    stream.next()
    val string = readWhile(_ != '"')
    if (stream.next() != '"') stream.croak("string parse error")
    StrToken(string)
  }


  def readNext(): Token = stream.peek match {
    case char if isWhitespace(char) => {stream.next(); readNext()}
    case '#' => {skipComment(); readNext()}
    case '"' => readString()
    case char if isNumber(char) => readNumber()
    case char if isIdStart(char) => readIdent()
    case char if isPunc(char) =>
    case char if isOperatorChar(char) =>
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
