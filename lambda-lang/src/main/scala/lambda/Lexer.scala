package lambda

/**
 * Created by zhoufeng on 16/3/21.
 */
class Lexer(val stream: InputStream) {
  val keywords = List("if", "then", "else", "labmda", "λ", "true", "false")
  val punctuation = List(",", ";", "(", ")", "{", "}", "[", "]")

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


