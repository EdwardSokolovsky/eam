import collection.JavaConverters._
import com.therandomlabs.curseapi.{CurseAPI, CurseException}
import properties.GeneralProperties._
import repo.AddonsFinder
import local.FilesChecker
import utils.{DateOperator, FileOperator}

object Main {

  private val localChecker = new FilesChecker(addonsFolderPath)

  def main(args: Array[String]): Unit = {
    localChecker.getLocalAddons.foreach(p => {
      val addonName = p.getFileName.toString
      val addon = AddonsFinder.findAddon(addonName).filter(
        a => a.name.equals(addonName)
      ).head
      val withoutClassic = addon.files.asScala.filter(f => !f.displayName.contains("classic"))
//      println(addon.files.asScala.foreach(f => f.gameVersionStrings().forEach(s => println(s))))
      val addonLastVersion = withoutClassic.toList.map(f => f.gameVersionStrings.asScala.toList.sortWith(_ > _).head).sortWith(_ > _).head
      val actualAddonFiles = withoutClassic.filter(f => f.gameVersionStrings.contains(addonLastVersion))
      val result = actualAddonFiles.toSeq.sortWith((s1, s2) => {
        val d1 = s1.uploadTime()
        val d2 = s2.uploadTime()
        d1.isAfter(d2)
      })
      //список проектов на сервере, соответствующий проектам на компе, отсортированный по свежести версий.
      println("-------------------------------")
      println("Download URL: " + result.head.downloadURL())
      println("Server Upload Time: " + result.head.uploadTime().toLocalDateTime)
      println("Local File creation date: " + DateOperator.getCreationTime(p))
      println("server is latest: " + result.head.uploadTime().toLocalDateTime.isAfter(DateOperator.getCreationTime(p)))
    })
  }

  private def getWowAddonsUrl: String = {
    val games = CurseAPI.games.get
    val wowGame = games.asScala.toSet.filter(g => g.id.equals(wowGameId)).toList.head
    val addonUrl = CurseAPI.project(CurseAPI.MIN_PROJECT_ID).get.categorySection.asCategory.url
    addonUrl.toString
  }

}

