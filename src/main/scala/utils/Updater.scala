package utils

import collection.JavaConverters._
import properties.GeneralProperties._
import repo.AddonsFinder
import local.FilesChecker
import okhttp3.HttpUrl
import scala.util.{Failure, Success, Try}

class Updater(addonsFolderPath: String) {

  private val localChecker = new FilesChecker(addonsFolderPath)

  def checkInstalledAddons(): Map[String, (HttpUrl, Boolean)] = {
    val buffer = scala.collection.mutable.Map[String, (HttpUrl, Boolean)]()
    localChecker.getLocalAddons.foreach(p => {
      val addonName = p.getFileName.toString
      val addon = AddonsFinder.findAddon(addonName).filter(
        a => a.name.equals(addonName)
      ).head
      val withoutClassic = addon.files.asScala.filter(f => !f.displayName.contains("classic"))
      val addonLastVersion = withoutClassic.toList.map(f => f.gameVersionStrings.asScala.toList.sortWith(_ > _).head).sortWith(_ > _).head
      val actualAddonFiles = withoutClassic.filter(f => f.gameVersionStrings.contains(addonLastVersion))
      val result = actualAddonFiles.toSeq.sortWith((s1, s2) => {
        val d1 = s1.uploadTime()
        val d2 = s2.uploadTime()
        d1.isAfter(d2)
      })
      val latestName = result.head.displayName()
      val latestUrl = result.head.downloadURL()
      val needToUpdate = result.head.uploadTime().toLocalDateTime.isAfter(DateOperator.getCreationTime(p))
      buffer += ((addonName, (latestUrl, needToUpdate)))
    })
    buffer.toMap
  }

  def update(addonsToUpdate: Map[String, HttpUrl], addonsFolder: String): Unit = {
    addonsToUpdate.foreach(addon => {
      val addonFolder = addonsFolder + slash + addon._1
      val addonZipFile = addonsFolder + slash + addon._1 + ".zip"
      val mayByFile = Try(FileOperator.downloadFile(addon._2, addonZipFile))
      mayByFile match {
        case Success(file) =>
          FileOperator.removeFile(addonFolder)
          FileOperator.unzip(addonZipFile, addonsFolder)
          FileOperator.removeFile(addonZipFile)
        case Failure(f)    => throw new Exception(f.getMessage)
      }
    })
  }


}
