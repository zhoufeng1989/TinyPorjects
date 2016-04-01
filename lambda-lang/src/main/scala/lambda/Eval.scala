package lambda

import java.util.NoSuchElementException

import scala.util.{Failure, Success, Try}

/**
 * Created by zhoufeng on 16/3/31.
 */
class Eval {

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
