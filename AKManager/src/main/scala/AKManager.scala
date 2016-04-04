/**
 * Created by zhoufeng on 16/4/4.
 */

case class Record(alias: String, id: String, secretKey: String, region: String="us-east-1")

class AKManager(val list: List[Record]) {
  def add(record: Record): AKManager = ???
  def delete(record: Record): AKManager = ???
}

class Config(config_file: String) {
  def loadFromFile(): List[Record] = ???
  def saveToFile() = ???
}

object AKManager extends App {

}
