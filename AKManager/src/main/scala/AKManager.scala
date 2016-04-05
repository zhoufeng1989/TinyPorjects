/**
 * Created by zhoufeng on 16/4/4.
 */

case class Record(alias: String, id: String, secretKey: String, region: String="us-east-1") {
  override def toString =
    List(s"alias=${alias}", s"id=${id}", s"secretKey=${secretKey}", s"region=${region}").mkString("\n")
}

class AKManager(val list: List[Record]) {
  def add(record: Record): AKManager = ???
  def delete(record: Record): AKManager = ???
}

class Config(config_file: String) {
  def loadFromFile(): List[Record] = ???
  def saveToFile() = ???
  def records = loadFromFile()
}

object AKManager extends App {
  val config = new Config(System.getProperty("os.home") + "/" + ".akm.cfg")
  val akManager = new AKManager(config.records)
}
