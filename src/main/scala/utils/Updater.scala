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
      val mayBeAddon = Try(AddonsFinder.getAddon(addonsFolderPath + slash + addonName).get)
      mayBeAddon match {
        case Failure(failure)      => println(s"Addon: '$addonName' not found!")
        case Success(curseProject) => {
          val addons = curseProject.files.asScala.toList
          val latestVersion = addons.map(f => {
            f.gameVersionStrings().asScala.toList
          }).filter(e => e.nonEmpty).map(p => p.head).distinct.sortWith(_>_).head
          val actualAddonFiles = addons.filter(f => f.gameVersionStrings.contains(latestVersion))
          val result = actualAddonFiles.sortWith((s1, s2) => {
            val d1 = s1.uploadTime()
            val d2 = s2.uploadTime()
            d1.isAfter(d2)
          })
          val latestName = result.head.displayName()
          val latestUrl = result.head.downloadURL()
          val needToUpdate = result.head.uploadTime().toLocalDateTime.isAfter(DateOperator.getCreationTime(p))
          buffer += ((addonName, (latestUrl, needToUpdate)))
        }
      }
    })
    buffer.toMap
  }

  def update(addonsToUpdate: Map[String, HttpUrl], addonsFolder: String): Unit = {
    addonsToUpdate.foreach(addon => {
      val addonFolder = addonsFolder + slash + addon._1
      val addonZipFile = addonsFolder + slash + addon._1 + archiveExtension
      val mayByFile = Try(FileOperator.downloadFile(addon._2, addonZipFile))
      mayByFile match {
        case Success(file) =>
          FileOperator.removeFileOrFolder(addonFolder)
          FileOperator.unzip(addonZipFile, addonsFolder)
          FileOperator.removeFileOrFolder(addonZipFile)
        case Failure(f)    => throw new Exception(f.getMessage)
      }
    })
  }


}
