/**
 * Created by zhoufeng on 16/4/4.
 */
import org.json4s._
import org.json4s.jackson.JsonMethods._
import scala.io.Source

case class Record(alias: String, id: String, secretKey: String, region: String="us-east-1") {
  override def toString =
    List(s"alias=${alias}", s"id=${id}", s"secretKey=${secretKey}", s"region=${region}").mkString("\n")
}

class AKManager(val list: List[Record]) {
  def add(record: Record): AKManager = ???
  def delete(record: Record): AKManager = ???
}

class Config(config_file: String) {
  def loadFromFile(): List[Record] = {
    val content = Source.fromFile(config_file).mkString("")
    println(parse(content).map(_.asInstanceOf[JObject]))
    List[Record]()
  }
  def saveToFile(records: List[Record]) = ???
  def records = loadFromFile()
}

object AKManager extends App {
  val config = new Config(System.getProperty("user.home") + "/" + ".akm.cfg")
  config.loadFromFile()
  val akManager = new AKManager(config.records)

}
