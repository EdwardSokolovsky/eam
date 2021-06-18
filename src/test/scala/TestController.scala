import org.scalatest.Suites
import suite.CheckAddonsTestSuite

//scalastyle:off
class TestController extends Suites (
  new CheckAddonsTestSuite()
)