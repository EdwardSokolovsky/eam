package configuration

object TestConfiguration {
  val addons: Map[Int, String] = Map(13501 -> "Bartender4", 4558 -> "Quartz", 17718 -> "Skada")
  val addonsFolderAbsPath: String = "C:\\Games\\World of Warcraft\\_retail_\\Interface\\AddOns"
  val oldAddonsDate: Map[String, Int] = Map(
    "yyyy" -> 1999,
    "MM" -> 8,
    "dd" -> 24,
    "HH" -> 11,
    "mm" -> 29,
    "ss" -> 17
  )
}
