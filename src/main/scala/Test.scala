import utils.Updater

object Test {

  private val addonsPath: String = "C:\\Games\\World of Warcraft\\_retail_\\Interface\\AddOns"

  def main(args: Array[String]): Unit = {
    val updater = new Updater(addonsPath)
    val result = updater.checkInstalledAddons()
    result.foreach(println)
  }
}
