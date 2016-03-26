package lambda

/**
 * Created by zhoufeng on 16/3/26.
 */
class Parser(val lexer: Lexer) {

  def parse(): List[ASTree] = lexer.peek match {
    case EofToken => List[ASTree]()
    case _ => {
      val expression = parseExpression()
      lexer.peek match {
        case EofToken => _
        case _ => skipToken(PuncToken(";"))
      }
      expression :: parse()
    }
  }

  def parseExpression():ASTree = maybeCall(maybeBinary(parseAtom(), 0))

  def maybeCall(ast: => ASTree): ASTree

  def maybeBinary(left: ASTree, precedence: Int): ASTree

  def parseAtom(): ASTree = lexer.peek match {
    case PuncToken("(") => {
      lexer.next();
      val ast = parseExpression();
      skipToken(PuncToken(")"))
      ast
    }
    case PuncToken("{") => parseProgram()
    case KeywordToken("if") => parseIf()
    case KeywordToken("true") | KeywordToken("false") => parseBool()
    case KeywordToken("lambda") | KeywordToken("λ") => {lexer.next(); parseLambda()}
    case NumToken(value) => {lexer.next(); Number(value)}
    case VarToken(value) => {lexer.next(); Var(value)}
    case StrToken(value) => {lexer.next(); Str(value)}
    case token => throw new ParseException(s"unexpected token ${token}")
  }

  def parseIf(): If = {
    lexer.next()
    val cond = parseExpression()
    val then = lexer.peek match {
      case PuncToken("{") => parseExpression()
      case _ => {skipToken(KeywordToken("then")); parseExpression()}
    }
    val els = lexer.peek match {
      case KeywordToken("else") => {lexer.next(); Some(parseExpression())}
      case _ => None
    }
    If(cond, then, els)
  }

  def parseBool(): Bool = lexer.next match {
    case KeywordToken("true") => Bool(true)
    case KeywordToken("false") => Bool(false)
  }

  def parseProgram(): ASTree

  def skipToken(token: Token):Unit = lexer.peek match {
    case t if t == token => {lexer.next()}
    case _ => throw new ParseException(s"Expecting token ${token}")
  }

}

class ParseException(message: String) extends RuntimeException(message)
