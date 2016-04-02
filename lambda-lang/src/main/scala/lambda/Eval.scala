package lambda

import java.util.NoSuchElementException

import scala.util.{Failure, Success, Try}

/**
 * Created by zhoufeng on 16/3/31.
 */
object Eval {
  def evaluate(expr: ASTree, env: Environment): Any = expr match {
    case Number(num) => num
    case Str(string) => string
    case Bool(b) => b
    case Var(v) => env.get(v)
  }
}

abstract class Environment {
  def get(key: String): Any
}

object Environment {
  case object EmptyEnv extends Environment {
    def get(key: String) = throw new EvalException(s"unbind ${key}")
  }

  case class Env(val parent: Environment, val vars: Map[String, Any]) extends Environment {
    def get(key: String): Any = Try(vars(key)) match {
      case Success(value) => value
      case Failure(ex) => parent.get(key)
    }
  }

  def apply(parent: Environment, vars: Map[String, Any]): Environment = Env(parent, vars)
}


class EvalException(message: String) extends RuntimeException(message)
