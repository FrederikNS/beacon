package dk.bestbrains.beacon

import com.codeminders.hidapi.{ClassPathLibraryLoader, HIDManager}
import dispatch._
import Defaults._
import net.liftweb.json._

object Beacon {
  implicit val formats = DefaultFormats

  val SET_STRUCTURE = Array[Byte](0x65, 0x02, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00)
  val SET_BYTE = 2

  val WHITE = 0xF0.asInstanceOf[Byte]
  val PURPLE = 0xF1.asInstanceOf[Byte]
  val CYAN = 0xF2.asInstanceOf[Byte]
  val BLUE = 0xF3.asInstanceOf[Byte]
  val YELLOW = 0xF4.asInstanceOf[Byte]
  val RED = 0xF5.asInstanceOf[Byte]
  val GREEN = 0xF6.asInstanceOf[Byte]
  val BLACK = 0xF7.asInstanceOf[Byte]

  def main(args: Array[String]) {
    ClassPathLibraryLoader.loadNativeHIDLibrary()
    val hid_mgr = HIDManager.getInstance()
    val device = hid_mgr.openById(0x0fc5, 0xb080, null)

    sys.ShutdownHookThread {
      val newStructure = SET_STRUCTURE.clone()
      newStructure.update(SET_BYTE, BLACK)
      device.sendFeatureReport(newStructure)
      device.close()
    }

    try {
      while(true) {
        val url2 = url(args(0))
        val body = Http(url2 OK as.String)
        val parsed = parse(body()).extract[HudsonProject]
        print("*")

        val newStructure = SET_STRUCTURE.clone()
        if(parsed.color == "red") {
          newStructure.update(SET_BYTE, RED)
        } else if(parsed.color == "blue") {
          newStructure.update(SET_BYTE, BLUE)
        } else if(parsed.color == "red_anime") {
          newStructure.update(SET_BYTE, PURPLE)
        } else if(parsed.color == "blue_anime") {
          newStructure.update(SET_BYTE, CYAN)
        } else {
          newStructure.update(SET_BYTE, WHITE)
        }

        device.sendFeatureReport(newStructure)
        Thread.sleep(if(args.length > 1) augmentString(args(1)).toInt else 1000)
      }
    } finally {
      val newStructure = SET_STRUCTURE.clone()
      newStructure.update(SET_BYTE, BLACK)
      device.sendFeatureReport(newStructure)
      device.close()
    }
  }
}
