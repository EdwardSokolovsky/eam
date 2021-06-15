package consts

object Messages {
  def pathDoesnotExistsMsg(path: String): String = s"Path: '$path' does not exists!"
  val somethingWrongMsg: String = "Something was wrong!"
  val doneMsg: String = "Done!"
  val defaultInfo = "No addons here."
}
