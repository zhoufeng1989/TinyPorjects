package lambda
/**
 * Created by zhoufeng on 16/3/26.
 */
abstract class ASTree {
  def evaluate(env: Map[String, Any]): Any
}

case class Number(value: Double) extends ASTree {
  def evaluate(env: Map[String, Any]):Double = value
}

case class Str(value: String) extends ASTree {
  def evaluate(env: Map[String, Any]): String = value
}

case class Bool(value: Boolean) extends ASTree {
  def evaluate(env: Map[String, Any]): Boolean = value
}

case class Var(value: String) extends ASTree  {
  def evaluate(env: Map[String, Any]) = env(value)
}

case class Lambda(vars: List[Var], body: ASTree) extends ASTree {
  def evaluate(env: Map[String, Any]) = {
    def lambda() = body.evaluate(env)
    () => lambda
  }
}

case class Call(func: Lambda, args: List[ASTree]) extends ASTree {
  def evaluate(env: Map[String, Any]) = {
    val vars = func.vars
    func.evaluate(env ++ vars.map(_.value).zip(args).toMap)()
  }
}

case class If(cond: ASTree, then: ASTree, els: Option[ASTree]) extends ASTree {
  def evaluate(env: Map[String, Any]) = {
    if (cond.evaluate(env) != false) {
      then.evaluate(env)
    }
    else {
      els.map(_.evaluate(env)) getOrElse false
    }
  }
}

case class Assign(left: ASTree, right: ASTree) extends ASTree {
  def evaluate(env: Map[String, Any]):Map[String, Any] = left.evaluate(env) match {
    case Var(value) => env ++ Map(value -> right.evaluate(env))
    case result => throw new EvalException(s"eval exception ${result}")
  }
}

case class Binary(operator: OpToken, left: ASTree, right: ASTree) extends ASTree {
  def evaluate(env: Map[String, Any]) = operator match {
    case OpToken("+") => left.evaluate(env).asInstanceOf[Double] + right.evaluate(env).asInstanceOf[Double]
    case OpToken("-") => left.evaluate(env).asInstanceOf[Double] - right.evaluate(env).asInstanceOf[Double]
    case OpToken("*") => left.evaluate(env).asInstanceOf[Double] * right.evaluate(env).asInstanceOf[Double]
    case OpToken("/") => left.evaluate(env).asInstanceOf[Double] / right.evaluate(env).asInstanceOf[Double]
    case OpToken("%") => left.evaluate(env).asInstanceOf[Double] % right.evaluate(env).asInstanceOf[Double]
    case OpToken("&&") => left.evaluate(env) match {
      case x if x != false => right.evaluate(env)
      case x => x
    }
    case OpToken("||") => left.evaluate(env) match {
      case x if x != false => x
      case _ => right.evaluate(env)
    }
    case OpToken("<") => left.evaluate(env).asInstanceOf[Double] < right.evaluate(env).asInstanceOf[Double]
    case OpToken("<=") => left.evaluate(env).asInstanceOf[Double] <= right.evaluate(env).asInstanceOf[Double]
    case OpToken(">") => left.evaluate(env).asInstanceOf[Double] > right.evaluate(env).asInstanceOf[Double]
    case OpToken(">=") => left.evaluate(env).asInstanceOf[Double] >= right.evaluate(env).asInstanceOf[Double]
    case OpToken("==") => left.evaluate(env) == right.evaluate(env)
    case OpToken("!=") => left.evaluate(env) != right.evaluate(env)
  }
}

case class Program(programs: List[ASTree]) extends ASTree {
  def evaluate(env: Map[String, Any]) =
    programs.foldLeft(false : Any)((any, ast) => ast.evaluate(env))
}


class EvalException(message: String) extends RuntimeException(message)
