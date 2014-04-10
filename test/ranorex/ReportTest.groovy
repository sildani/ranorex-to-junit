package ranorex

import org.junit.Test

public class ReportTest {

  @Test
  public void should_parse_report() {
    def file = new File("test/files/simple_report.data")
    def report = new Report(file.text)

    assert report.timestamp == '2014-04-09T17:29:56'
    assert report.duration == '1530.0'
    assert report.result == 'Success'
    assert report.host == 'CITU-W7-16168-0'
    assert report.suites.size() == 1
    assert report.suites[0].name == 'RRX_SubwayPOS'
    assert report.suites[0].testCases.size() == 1
    assert report.suites[0].testCases[0].name == 'CIT_POS_UPGRADE3.5'
    assert report.suites[0].testCases[0].duration == '1530.0'
    assert report.suites[0].testCases[0].type == 'test case'
    assert report.suites[0].testCases[0].errorcount == '2'
    assert report.suites[0].testCases[0].warningcount == '1'
    assert report.suites[0].testCases[0].successcount == '1'
    assert report.suites[0].testCases[0].failedcount == '0'
    assert report.suites[0].testCases[0].blockedcount == '0'
  }

  @Test
  public void should_export_to_junit_format() {

    def file = new File("test/files/failures.data")
    def report = new Report(file.text)


// <?xml version="1.0" encoding="UTF-8" ?>
// <testsuite hostname="pandaria.local" name="My_Cool_Testsuite" tests="5" failures="1" errors="0" time="162.0" timestamp="2014-03-29T20:05:58">
//   <properties>
//     <property name="Param1" value="" />
//     <property name="Param2" value="Value2" />
//   </properties>
//   <testcase classname="Outer Test Case - Inner Test Case - Module1" name="success" time="2.7" />
//   <testcase classname="Outer Test Case - Inner Test Case - Module2" name="success" time="120.0" />
//   <testcase classname="Outer Test Case - Inner Test Case - Module3" name="fail" time="120.0">
//     <failure type="Failed to find item">Message: No element found for path X within 2m. Failed to find item "The Great Gatsby". | Path: Xpath_expression | Stacktrace:    at Ranorex.Core.Repository.RepoItemInfo.Find[T](Boolean findSingle, Boolean throwException)    at Dashboard.NewFrontCounterRepositoryFolders.GenericViewFolder.get_ButtonFootlong()    at Dashboard.TestSuites.Modules.RemoteOrdering.VerifyRemoteOrderReceipt.Ranorex.Core.Testing.ITestModule.Run()    at Ranorex.Core.Testing.TestSuiteModule.RunInternal(DataContext parentDataContext)</failure>
//   </testcase>
//   <testcase classname="Outer Test Case - Inner Test Case - Module4" name="success" time="2.7" />
//   <testcase classname="Outer Test Case - Inner Test Case - Module5" name="success" time="2.7" />
// </testsuite>


    println report.toJunit()
  }

  
}
