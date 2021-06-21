import org.scalatest.Suites
import suite.AddonsUpdaterTestSuite

//scalastyle:off
class TestController extends Suites (
//  new AddonsUpdaterTestSuite(oldAddons = true),
  new AddonsUpdaterTestSuite(oldAddons = false)
)