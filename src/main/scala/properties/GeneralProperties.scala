package properties

import java.io.File

object GeneralProperties {

  val appName: String = "Edward's WoW Addon's Manager"
  val appVersion: String = "0.1"
  val addonsPackageFormat: String = ".zip"
  val wowGameId: Int = 1
  val slash: String = File.separator
  val testUrl: String = s"C:${slash}Games${slash}World of Warcraft${slash}_retail_${slash}Interface${slash}AddOns${slash}Bartender4"
  val addonsFolderPath: String = s"C:${slash}Games${slash}World of Warcraft${slash}_retail_${slash}Interface${slash}AddOns"
}
