package lambda

import scala.util.Either

/**
 * Created by zhoufeng on 16/3/21.
 */
class Lexer(val input: InputStream) {
  private val keywords = List("if", "then", "else", "lambda", "λ", "true", "false")
  private val punctuations = List(',', ';', '(', ')', '{', '}', '[', ']')
  private val operatorChars = List('+', '-', '*', '/', '%', '=', '&', '|', '<', '>', '!')
  private var current: Option[Token] = None

  private def isOperatorChar(char: Char): Boolean = operatorChars contains char
  private def isIdStart(char: Char): Boolean = if (char.isLetter || char == 'λ' || char == '_') true else false

  private def readWhile(predicate: Char => Boolean): String = input.peek() match {
    case Some(char) if predicate(char) => {input.next(); char + readWhile(predicate)}
    case _ => ""
  }

  private def skipComment():Unit = {
    readWhile(_ != '\n')
    input.next()
  }

  private def readNumber(): NumToken = {
    var dot = false
    def continueReadNumber(char: Char) = char match {
      case char if char.isDigit => true
      case '.' => if (dot) false else { dot = true; true}
      case _ => false
    }
    NumToken(readWhile(continueReadNumber).toDouble)
  }

  private def readString(): StrToken = {
    input.next()
    val string = readWhile(_ != '"')
    if (input.next() != Some('"')) input.croak("string parse error")
    StrToken(string)
  }

  private def readIdent(): Either[KeywordToken, VarToken] = {
    val id = readWhile(char => isIdStart(char) || char.isDigit || "?!-<>=".contains(char))
    if(keywords contains id) Left(KeywordToken(id)) else Right(VarToken(id))
  }

  private def readOperator(): OpToken = {
    val op = readWhile(isOperatorChar(_))
    OpToken(op)
  }

  private def readNext(): Token = input.peek match {
    case None => EofToken
    case Some(char) if char.isSpaceChar || char.isControl => {input.next(); readNext()}
    case Some('#') => {skipComment(); readNext()}
    case Some('"') => readString()
    case Some(char) if char.isDigit => readNumber()
    case Some(char) if isIdStart(char) => readIdent() match {case Left(token) => token; case Right(token) => token}
    case Some(char) if punctuations.contains(char) => {input.next(); PuncToken(char.toString)}
    case Some(char) if isOperatorChar(char) => readOperator()
    case token => input.croak(s"parse error ${token}")
  }

  def next(): Token = {
    if (current.isDefined) {
      val token = current.get
      current = None
      token
    } else readNext
  }

  def peek(): Token = {
    if (current.isEmpty) {
      current = Some(readNext)
    }
    current.get
  }

  def croak() = input.croak("parse error")
}


abstract class Token {
  val value: Any

}

case class NumToken(value: Double) extends Token


case class StrToken(value: String) extends Token


case class VarToken(value: String) extends Token


case class KeywordToken(value: String) extends Token


case class PuncToken(value: String) extends Token


case class OpToken(value: String) extends Token


case object EofToken extends Token {
  val value = ""
}
