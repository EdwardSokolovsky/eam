package scenario

import messages.TestMessages
import utils.FileOperator
import properties.GeneralProperties._

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

  protected[scenario] def compareLists(
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


}
