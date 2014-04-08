import org.junit.Before
import org.junit.Test
import org.custommonkey.xmlunit.Diff
import org.custommonkey.xmlunit.XMLUnit

class RanorexToJunitConverterTest {

  def converter

  @Before
  void setup() {
    converter = new RanorexToJunitConverter()
  }


  @Test
  void should_parse_testsuite() {
    def input = '''<?xml version="1.0" encoding="UTF-8"?>
<report>
  <activity user="user" host="pandaria.local" rxversion="4.0" osversion="Windows 7 Service Pack 1 32bit" runtimeversion="4.0" procarch="32bit" language="en-US" screenresolution="1024x768" timestamp="3/29/2014 8:05:58 PM" timeoutfactor="1" result="Success" duration="0ms" type="root" rid="132485f778cb2ecc" totalerrorcount="0" totalwarningcount="0" totalsuccesscount="1" totalfailedcount="0" totalblockedcount="0">
    <detail>Ranorex Testscript</detail>
    <activity testsuitename="My_Cool_Testsuite" runconfigname="Coolness" runlabel="" maxchildren="1" result="Success" duration="2.7m" type="test suite" rid="65a9b3684afb7da" totalerrorcount="0" totalwarningcount="0" totalsuccesscount="1" totalfailedcount="0" totalblockedcount="0">
      <params>
        <param name="Param1" />
        <param name="Param2">Value2</param>
      </params>
      <detail />
      <activity testcasename="Outer Test Case" testcaseid="059660b2-85da-4dbe-8a52-ee634a73caba" maxchildren="1" result="Success" duration="2.7m" type="test case" rid="bcd226b138cff16" totalerrorcount="0" totalwarningcount="0" totalsuccesscount="1" totalfailedcount="6" totalblockedcount="0">
        <params>
          <param name="Param3">Value3</param>
          <param name="Param4">Value4</param>
        </params>
        <detail />
        <activity testcasename="Inner Test Case" testcaseid="8f4c7cda-711d-403e-a719-b6f836ba23b6" maxchildren="0" result="Success" duration="2.7m" type="test case" rid="6d2ab1592db82c" totalerrorcount="0" totalwarningcount="0" totalsuccesscount="1" totalfailedcount="0" totalblockedcount="0">
          <params>
            <param name="Param5">Value5</param>
            <param name="Param6">Value6</param>
          </params>
          <detail />
          <activity modulename="Module" moduleid="d7aae740-28a8-4f9d-b6b0-6406c98ff4fd" moduletype="Module Type" result="Success" duration="2700ms" type="test module" rid="12f433bbe512c190" totalerrorcount="0" totalwarningcount="0" totalsuccesscount="0" totalfailedcount="0" totalblockedcount="0">
            <detail />
            <varbindings>
              <varbinding name="Varbinding1" value="Value" />
            </varbindings>
            <item time="02:07.000" level="Info" category="Data">
              <message>Some interesting message</message>
              <metainfo loglvl="Info" />
            </item>
          </activity>
        </activity>
      </activity>
    </activity>
  </activity>
</report>'''

    def expectedOutput = '''<?xml version="1.0" encoding="UTF-8" ?>
<testsuite hostname="pandaria.local" name="My_Cool_Testsuite" tests="1" failures="0" errors="0" time="162.0" timestamp="2014-03-29T20:05:58">
  <properties>
    <property name="Param1" value="" />
    <property name="Param2" value="Value2" />
  </properties>
  <testcase classname="Outer Test Case - Inner Test Case - Module" name="success" time="2.7" />
</testsuite>'''

    compareXml(input, expectedOutput)
  }

  @Test
  void should_parse_test_counts() {
    def input = '''<?xml version="1.0" encoding="UTF-8"?>
<report>
  <activity user="user" host="pandaria.local" rxversion="4.0" osversion="Windows 7 Service Pack 1 32bit" runtimeversion="4.0" procarch="32bit" language="en-US" screenresolution="1024x768" timestamp="3/29/2014 8:05:58 PM" timeoutfactor="1" result="Success" duration="0ms" type="root" rid="132485f778cb2ecc" totalerrorcount="0" totalwarningcount="0" totalsuccesscount="1" totalfailedcount="0" totalblockedcount="0">
    <detail>Ranorex Testscript</detail>
    <activity testsuitename="My_Cool_Testsuite" runconfigname="Coolness" runlabel="" maxchildren="1" result="Success" duration="2.7m" type="test suite" rid="65a9b3684afb7da" totalerrorcount="0" totalwarningcount="0" totalsuccesscount="1" totalfailedcount="0" totalblockedcount="0">
      <params>
        <param name="Param1" />
        <param name="Param2">Value2</param>
      </params>
      <detail />
      <activity testcasename="Outer Test Case" testcaseid="059660b2-85da-4dbe-8a52-ee634a73caba" maxchildren="1" result="Success" duration="2.7m" type="test case" rid="bcd226b138cff16" totalerrorcount="0" totalwarningcount="0" totalsuccesscount="1" totalfailedcount="6" totalblockedcount="0">
        <activity testcasename="Inner Test Case" testcaseid="8f4c7cda-711d-403e-a719-b6f836ba23b6" maxchildren="0" result="Success" duration="2.7m" type="test case" rid="6d2ab1592db82c" totalerrorcount="0" totalwarningcount="0" totalsuccesscount="1" totalfailedcount="0" totalblockedcount="0">
          <activity modulename="Module1" moduleid="moduleId1" moduletype="Module Type" result="Success" duration="2700ms" type="test module" rid="rid1" />
          <activity modulename="Module2" moduleid="moduleId2" moduletype="Module Type" result="Success" duration="2m" type="test module" rid="rid1" />
          <activity modulename="Module3" moduleid="moduleId3" moduletype="Module Type" result="Success" duration="2m" type="test module" rid="rid1" />
          <activity modulename="Module4" moduleid="moduleId4" moduletype="Module Type" result="Success" duration="2700ms" type="test module" rid="rid1" />
          <activity modulename="Module5" moduleid="moduleId5" moduletype="Module Type" result="Success" duration="2700ms" type="test module" rid="rid1" />
        </activity>
      </activity>
    </activity>
  </activity>
</report>'''

    def expectedOutput = '''<?xml version="1.0" encoding="UTF-8" ?>
<testsuite hostname="pandaria.local" name="My_Cool_Testsuite" tests="5" failures="0" errors="0" time="162.0" timestamp="2014-03-29T20:05:58">
  <properties>
    <property name="Param1" value="" />
    <property name="Param2" value="Value2" />
  </properties>
  <testcase classname="Outer Test Case - Inner Test Case - Module1" name="success" time="2.7" />
  <testcase classname="Outer Test Case - Inner Test Case - Module2" name="success" time="120.0" />
  <testcase classname="Outer Test Case - Inner Test Case - Module3" name="success" time="120.0" />
  <testcase classname="Outer Test Case - Inner Test Case - Module4" name="success" time="2.7" />
  <testcase classname="Outer Test Case - Inner Test Case - Module5" name="success" time="2.7" />
</testsuite>'''

    compareXml(input, expectedOutput)
  }

  @Test
  void should_parse_failures() {
    def input = '''<?xml version="1.0" encoding="UTF-8"?>
<report>
  <activity user="user" host="pandaria.local" rxversion="4.0" osversion="Windows 7 Service Pack 1 32bit" runtimeversion="4.0" procarch="32bit" language="en-US" screenresolution="1024x768" timestamp="3/29/2014 8:05:58 PM" timeoutfactor="1" result="Success" duration="0ms" type="root" rid="132485f778cb2ecc" totalerrorcount="0" totalwarningcount="0" totalsuccesscount="1" totalfailedcount="0" totalblockedcount="0">
    <detail>Ranorex Testscript</detail>
    <activity testsuitename="My_Cool_Testsuite" runconfigname="Coolness" runlabel="" maxchildren="1" result="Success" duration="2.7m" type="test suite" rid="65a9b3684afb7da" totalerrorcount="0" totalwarningcount="0" totalsuccesscount="1" totalfailedcount="0" totalblockedcount="0">
      <params>
        <param name="Param1" />
        <param name="Param2">Value2</param>
      </params>
      <detail />
      <activity testcasename="Outer Test Case" testcaseid="059660b2-85da-4dbe-8a52-ee634a73caba" maxchildren="1" result="Success" duration="2.7m" type="test case" rid="bcd226b138cff16" totalerrorcount="0" totalwarningcount="0" totalsuccesscount="1" totalfailedcount="6" totalblockedcount="0">
        <activity testcasename="Inner Test Case" testcaseid="8f4c7cda-711d-403e-a719-b6f836ba23b6" maxchildren="0" result="Success" duration="2.7m" type="test case" rid="6d2ab1592db82c" totalerrorcount="0" totalwarningcount="0" totalsuccesscount="1" totalfailedcount="0" totalblockedcount="0">
          <activity modulename="Module1" moduleid="moduleId1" moduletype="Module Type" result="Success" duration="2700ms" type="test module" rid="rid1" />
          <activity modulename="Module2" moduleid="moduleId2" moduletype="Module Type" result="Success" duration="2m" type="test module" rid="rid2" />
          <activity modulename="Module3" moduleid="moduleId3" moduletype="Module Type" result="Failed" duration="2m" type="test module" rid="rid3">
            <errmsg>Failed to find item</errmsg>
            <item time="20:55.029" level="Error" category="Module" errimg="error.jpg" errthumb="error_thumb.jpg">
              <message>
                No element found for path X within 2m.
                <br />
                Failed to find item "The Great Gatsby".
              </message>
              <metainfo type="repoitem" path="Xpath_expression" fullname="Something.Something_Else.Finally_Something" id="123" timeout="1" stacktrace="   at Ranorex.Core.Repository.RepoItemInfo.Find[T](Boolean findSingle, Boolean throwException)    at Dashboard.NewFrontCounterRepositoryFolders.GenericViewFolder.get_ButtonFootlong()    at Dashboard.TestSuites.Modules.RemoteOrdering.VerifyRemoteOrderReceipt.Ranorex.Core.Testing.ITestModule.Run()    at Ranorex.Core.Testing.TestSuiteModule.RunInternal(DataContext parentDataContext)" loglvl="Error" />
            </item>
          </activity>
          <activity modulename="Module4" moduleid="moduleId4" moduletype="Module Type" result="Success" duration="2700ms" type="test module" rid="rid4" />
          <activity modulename="Module5" moduleid="moduleId5" moduletype="Module Type" result="Success" duration="2700ms" type="test module" rid="rid5" />
        </activity>
      </activity>
    </activity>
  </activity>
</report>'''

    def expectedOutput = '''<?xml version="1.0" encoding="UTF-8" ?>
<testsuite hostname="pandaria.local" name="My_Cool_Testsuite" tests="5" failures="1" errors="0" time="162.0" timestamp="2014-03-29T20:05:58">
  <properties>
    <property name="Param1" value="" />
    <property name="Param2" value="Value2" />
  </properties>
  <testcase classname="Outer Test Case - Inner Test Case - Module1" name="success" time="2.7" />
  <testcase classname="Outer Test Case - Inner Test Case - Module2" name="success" time="120.0" />
  <testcase classname="Outer Test Case - Inner Test Case - Module3" name="fail" time="120.0">
    <failure type="Failed to find item">Message: No element found for path X within 2m. Failed to find item "The Great Gatsby". | Path: Xpath_expression | Stacktrace:    at Ranorex.Core.Repository.RepoItemInfo.Find[T](Boolean findSingle, Boolean throwException)    at Dashboard.NewFrontCounterRepositoryFolders.GenericViewFolder.get_ButtonFootlong()    at Dashboard.TestSuites.Modules.RemoteOrdering.VerifyRemoteOrderReceipt.Ranorex.Core.Testing.ITestModule.Run()    at Ranorex.Core.Testing.TestSuiteModule.RunInternal(DataContext parentDataContext)</failure>
  </testcase>
  <testcase classname="Outer Test Case - Inner Test Case - Module4" name="success" time="2.7" />
  <testcase classname="Outer Test Case - Inner Test Case - Module5" name="success" time="2.7" />
</testsuite>'''

    compareXml(input, expectedOutput)
  }

  @Test
  void should_parse_errors() {
    def input = '''<?xml version="1.0" encoding="UTF-8"?>
<report>
  <activity user="user" host="pandaria.local" rxversion="4.0" osversion="Windows 7 Service Pack 1 32bit" runtimeversion="4.0" procarch="32bit" language="en-US" screenresolution="1024x768" timestamp="3/29/2014 8:05:58 PM" timeoutfactor="1" result="Success" duration="0ms" type="root" rid="132485f778cb2ecc" totalerrorcount="0" totalwarningcount="0" totalsuccesscount="1" totalfailedcount="0" totalblockedcount="0">
    <detail>Ranorex Testscript</detail>
    <activity testsuitename="My_Cool_Testsuite" runconfigname="Coolness" runlabel="" maxchildren="1" result="Success" duration="2.7m" type="test suite" rid="65a9b3684afb7da" totalerrorcount="0" totalwarningcount="0" totalsuccesscount="1" totalfailedcount="0" totalblockedcount="0">
      <params>
        <param name="Param1" />
        <param name="Param2">Value2</param>
      </params>
      <detail />
      <activity testcasename="Outer Test Case" testcaseid="059660b2-85da-4dbe-8a52-ee634a73caba" maxchildren="1" result="Success" duration="2.7m" type="test case" rid="bcd226b138cff16" totalerrorcount="0" totalwarningcount="0" totalsuccesscount="1" totalfailedcount="6" totalblockedcount="0">
        <activity testcasename="Inner Test Case" testcaseid="8f4c7cda-711d-403e-a719-b6f836ba23b6" maxchildren="0" result="Success" duration="2.7m" type="test case" rid="6d2ab1592db82c" totalerrorcount="0" totalwarningcount="0" totalsuccesscount="1" totalfailedcount="0" totalblockedcount="0">
          <activity modulename="Module1" moduleid="moduleId1" moduletype="Module Type" result="Success" duration="2700ms" type="test module" rid="rid1" />
          <activity modulename="Module2" moduleid="moduleId2" moduletype="Module Type" result="Success" duration="2m" type="test module" rid="rid2" />
          <activity modulename="Module3" moduleid="moduleId3" moduletype="Module Type" result="Success" duration="2m" type="test module" rid="rid3">
            <item time="29:37.378" level="Info" category="Data">
              <message>
                Current variable values:
                <br />
                $Variable = true
              </message>
              <metainfo loglvl="Info" />
            </item>
            <item time="29:39.421" level="Error" category="User">
              <message>RefreshSystemSettings Failed: Unable to connect to the remote server</message>
              <metainfo loglvl="Error" />
            </item>
          </activity>
          <activity modulename="Module4" moduleid="moduleId4" moduletype="Module Type" result="Success" duration="2700ms" type="test module" rid="rid4" />
          <activity modulename="Module5" moduleid="moduleId5" moduletype="Module Type" result="Success" duration="2700ms" type="test module" rid="rid5" />
        </activity>
      </activity>
    </activity>
  </activity>
</report>'''

    def expectedOutput = '''<?xml version="1.0" encoding="UTF-8" ?>
<testsuite hostname="pandaria.local" name="My_Cool_Testsuite" tests="5" failures="0" errors="1" time="162.0" timestamp="2014-03-29T20:05:58">
  <properties>
    <property name="Param1" value="" />
    <property name="Param2" value="Value2" />
  </properties>
  <testcase classname="Outer Test Case - Inner Test Case - Module1" name="success" time="2.7" />
  <testcase classname="Outer Test Case - Inner Test Case - Module2" name="success" time="120.0" />
  <testcase classname="Outer Test Case - Inner Test Case - Module3" name="error" time="120.0">
    <error type="RefreshSystemSettings Failed: Unable to connect to the remote server">Current variable values: $Variable = true</error>
  </testcase>
  <testcase classname="Outer Test Case - Inner Test Case - Module4" name="success" time="2.7" />
  <testcase classname="Outer Test Case - Inner Test Case - Module5" name="success" time="2.7" />
</testsuite>'''

    compareXml(input, expectedOutput)
  }

  void compareXml(input, expectedOutput) {
    def output = converter.convert(input)
    println output
    XMLUnit.setIgnoreWhitespace(true)
    def xmlDiff = new Diff(output, expectedOutput)
    assert xmlDiff.similar()
  }

  @Test
  void should_parse_ranoerex_timestamp() {
    assert converter.parseTimestamp('3/29/2014 8:05:58 PM') == '2014-03-29T20:05:58'
    assert converter.parseTimestamp('12/31/2013 11:59:59 PM') == '2013-12-31T23:59:59'
  }

  @Test
  void should_parse_ranorex_duration() {
    assert converter.parseDuration('0ms') == '0.0'
    assert converter.parseDuration('1.1h') == '3960.0'
    assert converter.parseDuration('2.7m') == '162.0'
    assert converter.parseDuration('29.7m') == '1782.0'
    assert converter.parseDuration('4.6m') == '276.0'
    assert converter.parseDuration('2m') == '120.0'
    assert converter.parseDuration('10.53s') == '10.5'
    assert converter.parseDuration('6848ms') == '6.8'
    assert converter.parseDuration('2700ms') == '2.7'
  }


  @Test(expected=IllegalArgumentException)
  void should_throw_exception_if_ranorex_duration_not_understood() {
    converter.parseDuration('not a good value')
  }

  @Test
  void should_strip_out_html_and_line_feeds() {
    assert converter.parseText('hello <br /> world') == 'hello world'
    assert converter.parseText('hello\nworld') == 'hello world'
    assert converter.parseText('    Hello,   world. Pleasure to    meet you.') == 'Hello, world. Pleasure to meet you.'
  }

}