package repo

import properties.GeneralProperties._
import scala.compat.java8.OptionConverters._
import com.therandomlabs.curseapi.CurseAPI
import com.therandomlabs.curseapi.project.{CurseProject, CurseSearchQuery}
import scala.io.Source

object AddonsFinder {

  def getAddon(addonFolderAbsPath: String): Option[CurseProject] = {
    val curseProject = CurseAPI.project(getAddonId(addonFolderAbsPath))
    curseProject.asScala
  }

  private def getAddonId(addonFolderAbsPath: String): Int = {
    val metainf = addonFolderAbsPath + slash + addonFolderAbsPath.split('\\').last + metainfExtension
    val titleLine = Source.fromFile(metainf).getLines.filter(p => p.contains(addonIdName)).toList.head
    titleLine.split(addonIdName).last.toInt
  }

}
