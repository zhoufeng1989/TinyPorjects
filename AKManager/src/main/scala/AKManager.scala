/**
 * Created by zhoufeng on 16/4/4.
 */
import org.json4s._
import org.json4s.jackson.JsonMethods._
import scala.io.Source
import scala.io.StdIn._

abstract class RecordClass {
  val alias: String
  val aws_access_key_id: String
  val aws_secret_access_key: String
  val region: String
}

case class Record(alias: String, aws_access_key_id: String, aws_secret_access_key: String, region: String="us-east-1") extends RecordClass {
  override def toString =
    List(
      s"${alias} ",
      List(
        s"aws_access_key_id: ${aws_access_key_id}",
        s"aws_secret_access_key: ${aws_secret_access_key}",
        s"region: ${region}").map("  " + _).mkString("\n")
    ).mkString("\n")
}

case object emptyRecord extends RecordClass {
  val alias = "None"
  val aws_access_key_id = "None"
  val aws_secret_access_key = "None"
  val region = "None"
}

class Config(config_file: String) {
  def loadFromFile(): List[Record] = {
    val source = Source.fromFile(config_file)
    parse(source.reader()).asInstanceOf[JObject].obj.map {
      case (key: String, value:JValue) => Record(key, compact(render(value \\ "aws_access_key_id")), compact(render(value \\ "aws_secret_access_key")))
    }
  }
  def saveToFile(records: List[Record]) = records.map(
    record => (
      record.alias -> Map(
        "aws_access_key_id" -> record.aws_access_key_id,
        "aws_secret_access_key" -> record.aws_secret_access_key,
        "region" -> record.region)
    )
  ).toMap
  def records = loadFromFile()
}

object AKManager extends App {
  val config = new Config(System.getProperty("user.home") + "/" + ".akm.cfg")
  val records = config.records
  args match {
    case Array("list") => show(records)
    case Array("delete", xs) => config.saveToFile(records.filterNot(record => xs.contains(record.alias)))
    case Array("add", alias) => config.saveToFile(add(records, alias))
  }

  def show(records: List[Record]): Unit = records match {
    case l if l.isEmpty => println("Non entry found")
    case _ => println(records.mkString("\n"))
  }

  def add(records: List[Record], alias: String) = {
    val record = records.find(_.alias == alias) getOrElse emptyRecord
    val aws_access_key_id = readLine(s"AWS Access Key ID [${record.aws_access_key_id}]: ")
    val aws_secret_access_key = readLine(s"AWS Secret Access Key [${record.aws_secret_access_key}]: ")
    val region = readLine(s"Default region name [${record.region}]: ")
    Record(alias, aws_access_key_id, aws_secret_access_key, region) :: records.filter(_ != record)
  }
}
