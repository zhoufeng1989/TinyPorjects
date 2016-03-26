package lambda

/**
 * Created by zhoufeng on 16/3/26.
 */
class ASTree

case class Number(value: Double) extends ASTree

case class Str(value: String) extends ASTree

case class Bool(value: Boolean) extends ASTree

case class Var(value: String) extends ASTree

case class Operator(value: String) extends ASTree

case class Lambda(vars: List[Var], body: ASTree) extends ASTree

case class Call(func: ASTree, args: List[ASTree]) extends ASTree

case class If(cond: ASTree, then: ASTree, els: Option[ASTree]) extends ASTree

case class Assign(left: ASTree, right: ASTree) extends ASTree

case class Binary(operator: Operator, left: ASTree, right: ASTree) extends ASTree

case class Program(programs: List[ASTree]) extends ASTree

