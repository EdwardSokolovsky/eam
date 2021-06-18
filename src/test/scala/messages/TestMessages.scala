package messages

import org.apache.logging.log4j.scala.Logging
import scala.io.AnsiColor._

trait TestMessages extends Logging{

  private val Y = YELLOW
  private val R = RED
  private val G = GREEN
  private val RS = RESET
  private val YRVB = s"$Y$REVERSED$BOLD"
  private val GRVB = s"$G$REVERSED$BOLD"
  private val RRVB = s"$R$REVERSED$BOLD"

  def allTestRunningMessage(testSuiteName: String): Unit = {
    colorBorder(Y, s"$YRVB$testSuiteName TESTS SUITE RUNNING.$RS")
  }
  def testRunningMessage(testName: String): Unit = {
    colorBorder(G, s"$GRVB$testName test running.$RS")
  }
  def testSuccessMessage(testName: String): Unit = {
    colorBorder(G, s"$GRVB$testName test ended successful!$RS")
  }
  def testFailedMessage(testName: String): Unit = {
    colorBorder(R, s"$RRVB$testName test failed!$RS")
  }
  def allTestsPassedMessage(testName: String): Unit = {
    colorBorder(Y, s"$YRVB$testName TESTS SUITE PASSED SUCCESSFUL!$RS")
  }
  def someTestsFailedMessage(testName: String): Unit = {
    colorBorder(R, s"$RRVB$testName TESTS SUITE WAS FAIL!$RS")
  }
  private def colorBorder(color: String, message: String): Unit = {
    logger.info(s"$color=======================================================$RS")
    logger.info(message)
    logger.info(s"$color=======================================================$RS")
  }
}

