package suite

import org.scalatest.{BeforeAndAfterAll, DoNotDiscover}
import org.scalatest.funsuite.AnyFunSuite
import scenario.BaseScenario
import configuration.TestConfiguration._
import utils.Updater
@DoNotDiscover
class CheckAddonsTestSuite extends AnyFunSuite
  with BaseScenario
  with BeforeAndAfterAll {

  private val testsSuiteName: String = "UPDATER"
  private val checkInstalledAddonsTestname: String = "checkInstalledAddonsTest"
  private var allTestsPassed: Boolean = true

  override def beforeAll(): Unit = {
    allTestRunningMessage(testsSuiteName)
    cleanBeforeTest(addonsFolderAbsPath, addons)
    generateAddons(addonsFolderAbsPath, addons)
  }

  override def afterAll(): Unit = {
    afterTestsMessage(allTestsPassed, testsSuiteName)
  }

  test(checkInstalledAddonsTestname){
    val updater = new Updater(addonsFolderAbsPath)
    val expected = addons.values
    val actual = updater.checkInstalledAddons().keys
    val result = compareLists(expected, actual)
    allTestsPassed = testEnded(result, checkInstalledAddonsTestname)
    assert(result)
  }

}
