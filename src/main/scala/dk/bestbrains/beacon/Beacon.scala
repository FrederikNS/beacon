package dk.bestbrains.beacon

import com.codeminders.hidapi.{ClassPathLibraryLoader, HIDManager}
import dispatch._
import net.liftweb.json._
import scala.util.control.Breaks._


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

    sys.ShutdownHookThread {
      val device = hid_mgr.openById(0x0fc5, 0xb080, null)
      if(device == null) return
      val newStructure = SET_STRUCTURE.clone()
      newStructure.update(SET_BYTE, BLACK)
      device.sendFeatureReport(newStructure)
      device.close()
    }

    if(args.length < 1) {
      println("Url needs to be specified in the first argument")
      return
    }

    val device = hid_mgr.openById(0x0fc5, 0xb080, null)
    while(true) {
      try {
        var old_color: String = ""
        while(true) {
          val body = Http(url(args(0)) OK as.String)
          var parsed = parse(body()).extract[HudsonProject]
          if(args.length ==3) {
            val buildbody = Http(url(args(1)) OK as.String)
            val testbody = Http(url(args(2)) OK as.String)

            val build = parse(buildbody()).extract[HudsonProject]
            val test = parse(testbody()).extract[HudsonProject]
            var level = findLevel(parsed.color)
            if(findLevel(build.color) > level) {
              level = findLevel(build.color)
              parsed = build
            }
            if(findLevel(test.color) > level) {
              level = findLevel(test.color)
              parsed = test
            }
          }
          print("*")

            if(parsed.color != old_color) {
            val newStructure = SET_STRUCTURE.clone()
            matchTest(parsed.color, newStructure)

            device.sendFeatureReport(newStructure)
            old_color = parsed.color
          }

          Thread.sleep(if(args.length == 2) augmentString(args(1)).toInt else 1000)
        }
      } catch {
        case ie: InterruptedException => {
          return
        }
        case e: Exception => {
          e.printStackTrace()
          val newStructure = SET_STRUCTURE.clone()
          newStructure.update(SET_BYTE, WHITE)
          device.sendFeatureReport(newStructure)
          Thread.sleep(if(args.length == 2) augmentString(args(1)).toInt else 1000)
        }
      } finally {
        device.close()
      }
    }
  }

  def findLevel(color: Any): Int = color match {
      case "red" => 1
      case "blue" => 0
      case "red_anime" => 2
      case "blue_anime" => 2
      case "green" => 0
      case "green_anime" => 2
      case _ => 2
  }

  def matchTest(color: Any, newStructure: Array[Byte]): Any = color match {
    case "red" => newStructure.update(SET_BYTE, RED)
      case "blue" => newStructure.update(SET_BYTE, BLUE)
      case "red_anime" => newStructure.update(SET_BYTE, PURPLE)
      case "blue_anime" => newStructure.update(SET_BYTE, CYAN)
      case "green" => newStructure.update(SET_BYTE, GREEN)
      case "green_anime" => newStructure.update(SET_BYTE, YELLOW)
      case _ => newStructure.update(SET_BYTE, WHITE)
  }
}
