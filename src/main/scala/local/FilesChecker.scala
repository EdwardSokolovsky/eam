package local

import java.nio.file.{Files, Path, Paths}
import collection.JavaConverters._

class FilesChecker(val addonsFolderAbsPath: String) {

  private val addonsPath = Paths.get(addonsFolderAbsPath)

  require(Files.exists(addonsPath), s"Folder '$addonsFolderAbsPath' doesnt exists!")
  require(Files.isDirectory(addonsPath), s"'$addonsFolderAbsPath' is not directory!")
  require(Files.isWritable(addonsPath), s"'$addonsFolderAbsPath' permission denied (write)!")
  require(Files.isReadable(addonsPath), s"'$addonsFolderAbsPath' permission denied (read)!")

  def getLocalAddons: List[Path] = {
    val addons =  Files.list(addonsPath).iterator().asScala.toList
    addons.filter(p => Files.isDirectory(p) && !p.getFileName.toString.contains("_"))
  }

}
