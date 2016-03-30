package lambda

/**
 * Created by zhoufeng on 16/3/26.
 */
class Parser(val lexer: Lexer) {

  private val precedences = Map(
    OpToken("=") -> 1, OpToken("||") -> 2, OpToken("&&") -> 3, OpToken("<") -> 7, OpToken(">") -> 7,
    OpToken("<=") -> 7, OpToken(">=") -> 7, OpToken("==") -> 7, OpToken("!=") -> 7, OpToken("+") -> 10,
    OpToken("-") -> 10, OpToken("*") -> 20, OpToken("/") -> 20, OpToken("%") -> 20)

  def parse(): List[ASTree] = lexer.peek match {
    case EofToken => List[ASTree]()
    case _ => {
      val expression = parseExpression()
      lexer.peek match {
        case EofToken => ()
        case _ => skipToken(PuncToken(";"))
      }
      expression :: parse()
    }
  }

  def parseExpression():ASTree = maybeCall(maybeBinary(parseAtom(), 0))

  def maybeCall(ast: => ASTree): ASTree = {
    val expr = ast
    lexer.peek match {
      case PuncToken("(") => parseCall(expr)
      case _ => expr
    }
  }

  def parseCall(func: ASTree): ASTree = Call(func, delimited(PuncToken("("), PuncToken(")"), PuncToken(","), parseExpression()))

  def maybeBinary(left: ASTree, precedence: Int): ASTree = lexer.peek match {
    case token: OpToken => {
      lexer.next()
      val right = parseAtom()
      lexer.peek match {
        case nextToken: OpToken => {
          val nextPrecedence = precedences(token)
          if (nextPrecedence > precedence) {
            Binary(Operator(token.value), left, maybeBinary(right, nextPrecedence))
          }
          else {
            maybeBinary(Binary(Operator(token.value), left, right), nextPrecedence)
          }
        }
        case _ => Binary(Operator(token.value), left, right)
      }
    }
    case _ => left
  }

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
    case token => lexer.croak()
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

  def parseLambda(): Lambda = Lambda(
    delimited(PuncToken("("), PuncToken(")"), PuncToken(","), parseVar).asInstanceOf[List[Var]],
    parseExpression()
  )

  def delimited(start: PuncToken, end: PuncToken, delimiter: PuncToken, ast: => ASTree): List[ASTree] = {
    skipToken(start)

    def _delimited(first: Boolean): List[ASTree] = lexer.peek match {
      case token if token == end => {lexer.next(); List[Var]()}
      case token if token == delimiter => {
        if (first)
          throw new ParseException(s"unexpected token ${token}")
        else{
          skipToken(delimiter);
          _delimited(false)
        }
      }
      case _ => ast :: _delimited(false)
    }

    _delimited(true)
  }

  def parseVar(): Var = lexer.next match {
    case VarToken(varname) => Var(varname)
    case _ => throw new ParseException("expecting variable name")
  }

  def parseProgram(): Program = Program(delimited(PuncToken("{"), PuncToken("}"), PuncToken(";"), parseExpression()))

  def skipToken(token: Token):Unit = lexer.peek match {
    case t if t == token => {lexer.next()}
    case _ => throw new ParseException(s"Expecting token ${token}")
  }

}

class ParseException(message: String) extends RuntimeException(message)
