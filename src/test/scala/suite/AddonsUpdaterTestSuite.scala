package suite

import org.scalatest.{BeforeAndAfterAll, DoNotDiscover}
import org.scalatest.funsuite.AnyFunSuite
import scenario.BaseScenario
import configuration.TestConfiguration._
import properties.GeneralProperties._
import utils.Updater
import java.time.LocalDateTime
@DoNotDiscover
class AddonsUpdaterTestSuite(oldAddons: Boolean) extends AnyFunSuite
  with BaseScenario
  with BeforeAndAfterAll {

  private val testsSuiteName: String = s"ADDON'S UPDATER - OLD ADDON'S: $oldAddons"
  private val checkInstalledAddonsTestName: String = "checkInstalledAddonsTest"
  private val checkAddonsRelevanceTestName: String = "checkAddonsRelevanceTest"
  private val checkAddonsUpdateTestName: String = "checkAddonsUpdateTest"
  private var allTestsPassed: Boolean = true
  private val updater = new Updater(addonsFolderAbsPath)

  override def beforeAll(): Unit = {
    allTestRunningMessage(testsSuiteName)
    cleanBeforeTest(addonsFolderAbsPath, addons)
    generateAddons(addonsFolderAbsPath, addons)
    if (oldAddons) {
      addons.foreach(addon => {
        val path = addonsFolderAbsPath + slash + addon._2
        val creationDate = LocalDateTime.of(
          oldAddonsDate("yyyy"),
          oldAddonsDate("MM"),
          oldAddonsDate("dd"),
          oldAddonsDate("HH"),
          oldAddonsDate("mm"),
          oldAddonsDate("ss")
        )
        setCreationTime(path, creationDate)
      })
    }
  }

  override def afterAll(): Unit = {
    afterTestsMessage(allTestsPassed, testsSuiteName)
  }

  test(checkInstalledAddonsTestName){
    testRunningMessage(checkInstalledAddonsTestName)
    val expected = addons.values
    val actual = updater.checkInstalledAddons().keys
    val result = compareCollection(expected, actual)
    allTestsPassed = testEnded(result, checkInstalledAddonsTestName)
    assert(result)
  }

  test(checkAddonsRelevanceTestName){
    testRunningMessage(checkAddonsRelevanceTestName)
    val result = updater.checkInstalledAddons().forall(p => p._2._2 == oldAddons)
    allTestsPassed = testEnded(result, checkAddonsRelevanceTestName)
    assert(result)
  }

  test(checkAddonsUpdateTestName){
    testRunningMessage(checkAddonsUpdateTestName)
    val addons = updater.checkInstalledAddons()
    val lastModBeforeUpdates = addons.map(p => (p._1, getLastModified(addonsFolderAbsPath + slash + p._1)))
    updater.update(addons, addonsFolderAbsPath)
    val lastModAfterUpdates = addons.map(p => (p._1, getLastModified(addonsFolderAbsPath + slash + p._1)))
    val result =
    if(oldAddons){
      lastModAfterUpdates.forall(p => {
        val dateBeforeUpdates = fileTimeToLocalDateTime(lastModBeforeUpdates(p._1))
        val dateAfterUpdates = fileTimeToLocalDateTime(p._2)
        dateAfterUpdates.isAfter(dateBeforeUpdates)
      })
    } else {
      compareMap(lastModBeforeUpdates, lastModAfterUpdates)
    }
    allTestsPassed = testEnded(result, checkAddonsUpdateTestName)
    assert(result)
  }

}
