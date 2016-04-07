/**
 * Created by zhoufeng on 16/4/4.
 */
import org.json4s._
import org.json4s.jackson.JsonMethods._
import scala.io.Source

case class Record(alias: String, aws_access_key_id: String, aws_secret_access_key: String, region: String="us-east-1") {
  override def toString =
    List(
      s"${alias} ",
      List(
        s"aws_access_key_id: ${aws_access_key_id}",
        s"aws_secret_access_key: ${aws_secret_access_key}",
        s"region: ${region}").map("  " + _).mkString("\n")
    ).mkString("\n")
}

class Config(config_file: String) {
  def loadFromFile(): List[Record] = {
    val source = Source.fromFile(config_file)
    parse(source.reader()).asInstanceOf[JObject].obj.map {
      case (key: String, value:JValue) => Record(key, compact(render(value \\ "aws_access_key_id")), compact(render(value \\ "aws_secret_access_key")))
    }
  }
  def saveToFile(records: List[Record]) = println(records)
  def records = loadFromFile()
}

object AKManager extends App {
  val config = new Config(System.getProperty("user.home") + "/" + ".akm.cfg")
  val records = config.records
  args match {
    case Array("list") => show(records)
    case Array("delete", xs) => config.saveToFile(records.filterNot(record => xs.contains(record.alias)))
    case Array("add") => add(records)
  }

  def show(records: List[Record]): Unit = records match {
    case l if l.isEmpty => println("Non entry found")
    case _ => println(records.mkString("\n"))
  }

  def add(records: List[Record]) = ???
}
