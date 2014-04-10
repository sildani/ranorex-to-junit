package ranorex

import org.custommonkey.xmlunit.Diff
import org.custommonkey.xmlunit.XMLUnit

import org.junit.Before
import org.junit.Test

public class ReportTest {

  @Before
  void before() {
    XMLUnit.setIgnoreWhitespace(true)
  }

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
  public void should_export_failures_to_junit_format() {

    def file = new File("test/files/failures.data")
    def report = new Report(file.text)
    def expected = '''<?xml version="1.0" encoding="UTF-8"?><testsuite hostname='hostname' name='RRX_SubwayPOS' tests='1' failures='00111' errors='00211' time='498.0' timestamp='2014-04-09T21:22:45'>
      <testcase classname='RRX_SubwayPOS' name='TestCase_CI_Guaranteed_Upgrade_Setup' time='198.0' />
      <testcase classname='RRX_SubwayPOS' name='TestCase_CI_Verify_PosSetup_Given_DashboardSetup_DoesNotExist' time='1.4' />
      <testcase classname='RRX_SubwayPOS' name='TestCase_CI_Verify_Dashboard_Upgrade_Given_PreviousRelease_DashboardSetup' time='78.0'>
        <failure type='VerifySplunkConfigFilesDeployed'>SplunkForwarder configuration files are not deployed successfully</failure>
        <failure type='VerifyPosSetupWhenDasbhoardSetupExists'>Shortcuts are not updated</failure>
      </testcase>
      <testcase classname='RRX_SubwayPOS' name='TestCase_CI_Verify_Dashboard_Upgrade_From_NewStructure_Given_Latest_DashboardSetup' time='108.0'>
        <failure type='VerifyPosSetupWhenDasbhoardSetupExists'>Shortcuts are not updated</failure>
      </testcase>
      <testcase classname='RRX_SubwayPOS' name='TestCase_CI_Verify_Dashboard_Upgrade_From_OldStructure_Given_Latest_DashboardSetup' time='114.0'>
        <failure type='VerifyPosSetupWhenDasbhoardSetupExists'>Shortcuts are not updated</failure>
      </testcase>
    </testsuite>'''

    assert new Diff(report.toJunit(), expected).identical()
  }

}
