package scenario

import messages.TestMessages
import utils.FileOperator
import properties.GeneralProperties._
import java.nio.file.attribute.BasicFileAttributeView
import java.nio.file.attribute.FileTime
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.nio.file.{Files, Paths}
import java.time.ZoneId

trait BaseScenario extends TestMessages {

   protected[scenario] def generateAddons(addonsFolderAbsPath: String, addons: Map[Int, String]): Unit = {
     addons.foreach(idAndName => {
       FileOperator.mkdirRecursive(addonsFolderAbsPath + slash + idAndName._2)
       generateMetainf(addonsFolderAbsPath, idAndName)
     })
  }

  private def generateMetainf(addonsFolderAbsPath: String, nameAndId: (Int, String)): Unit = {
    val metainfPath = addonsFolderAbsPath + slash + nameAndId._2 + slash + nameAndId._2 + metainfExtension
    val text = addonIdName + nameAndId._1
    FileOperator.writeTextFile(text, metainfPath)
  }

  protected[scenario] def cleanBeforeTest(
    addonsFolderAbsPath: String,
    addons: Map[Int, String]
  ): Unit = {
    addons.foreach(idAndName => {
      FileOperator.removeFileOrFolder(addonsFolderAbsPath + slash + idAndName._2)
    })
  }

  protected[scenario] def testEnded(result: Boolean, testName: String): Boolean = {
    if(result){
     testSuccessMessage(testName)
    } else {
      testFailedMessage(testName)
    }
    result
  }

  protected[scenario] def afterTestsMessage(
   allTestsPassed: Boolean,
   testsSuiteName: String
  ): Unit = {
    if(allTestsPassed){
      allTestsPassedMessage(testsSuiteName)
    } else {
      someTestsFailedMessage(testsSuiteName)
    }
  }

  protected[scenario] def compareCollection(
   expected: Iterable[Any],
   actual: Iterable[Any]
  ): Boolean = {
    val diff = actual.toSet.diff(expected.toSet)
    if(diff.nonEmpty){
      logger.error("Different: ")
      diff.foreach(p => logger.error(p.toString))
      false
    } else {
      true
    }
  }

  protected[scenario] def compareMap(
   expected: Map[String, FileTime],
   actual: Map[String, FileTime]
  ): Boolean = {
    val diff = (expected.toSet diff actual.toSet).toMap
    if(diff.nonEmpty){
      logger.info("Difference: ")
      diff.foreach(println)
    }
    diff.isEmpty
  }

  protected[scenario] def fileTimeToLocalDateTime(fileTime: FileTime): LocalDateTime = {
    LocalDateTime.ofInstant(fileTime.toInstant, ZoneId.systemDefault)
  }

  protected[scenario] def setLastModified(fileAbsPath: String, dateTime: LocalDateTime): Unit = {
    val path = Paths.get(fileAbsPath)
    val instant = dateTime.toInstant(ZoneOffset.UTC)
    Files.setLastModifiedTime(path, FileTime.from(instant))
  }

  protected[scenario] def setCreationTime(fileAbsPath: String, dateTime: LocalDateTime): Unit = {
    val attributes = Files.getFileAttributeView(Paths.get(fileAbsPath), classOf[BasicFileAttributeView])
    val time = FileTime.from(dateTime.toInstant(ZoneOffset.UTC))
    attributes.setTimes(time, time, time)
  }

  protected[scenario] def getLastModified(fileAbsPath: String): FileTime = {
    val attributes = Files.getFileAttributeView(Paths.get(fileAbsPath), classOf[BasicFileAttributeView])
    attributes.readAttributes.lastModifiedTime
  }


}
