import groovy.xml.MarkupBuilder

class RanorexToJunitConverter {
  
  def convert(ranorexXml) {
    def ranorex = new XmlSlurper().parseText(ranorexXml).activity[0]

    def hostname = ranorex.@host.text()
    def timestamp = parseTimestamp(ranorex.@timestamp.text())
    def testsuite = ranorex.activity[0]

    def writer = new StringWriter()
    def junit = new MarkupBuilder(writer)

    def testCases = [:]
    def failures = [:]

    testsuite.activity.each { outerTestCase ->
      outerTestCase.activity.each { innerTestCase ->
        innerTestCase.activity.each { module ->
          def key = "${outerTestCase.@testcasename.text()} - ${innerTestCase.@testcasename.text()} - ${module.@modulename.text()}"
          def result = module.@result.text().toLowerCase()
          if (result == 'failed') {
            def errorItem = module.item.findAll { it.@level.text() == 'Error' }[0]
            failures.put(key, [type: module.errmsg.text(), message: errorItem.message.text(), path: errorItem.metainfo.@path.text(), stacktrace: errorItem.metainfo.@stacktrace.text()])
          }
          testCases.put(key, [name: result, time: parseDuration(module.@duration.text())])
        }
      }
    }

    writer.write '<?xml version="1.0" encoding="UTF-8"?>'
    junit.testsuite(hostname: hostname, name: testsuite.@testsuitename.text(), tests: testCases.size(), failures: failures.size(), errors: 0, time: parseDuration(testsuite.@duration.text()), timestamp: timestamp) {
      properties() {
        testsuite.params.param.each {
          property(name: it.@name.text(), value: it.text())
        }
      }
      testCases.each { classname, testCase ->
        testcase(classname: classname, name: testCase.name, time: testCase.time) {
          if (failures.keySet().contains(classname)) {
            def f = failures.get(classname)
            StringBuffer b = new StringBuffer()
            failure(type: f.type, "Message: ${parseText(f.message)} | Path: ${f.path} | Stacktrace: ${f.stacktrace}")
          }
        }
      }
    }

    writer.toString()
  }

  def parseTimestamp(ranorexTimestamp) {
    def ranorexDate = Date.parse('M/d/yyyy h:m:s a', ranorexTimestamp)
    ranorexDate.format('yyyy-MM-dd') + "T" + ranorexDate.format('HH:mm:ss')
  }

  def parseDuration(ranorexDuration) {
    if (ranorexDuration =~ /^[0-9]*\.[0-9]*m$/) {
      // dealing in minutes
      def marker = ranorexDuration.indexOf('.')
      def end = ranorexDuration.length() - 1
      return "${ranorexDuration.substring(0, marker)}.${ranorexDuration.substring(marker+1, end).padRight(3, '0')}"
    }
    if (ranorexDuration =~ /^[0-9]*m$/) {
      // dealing in minutes
      def end = ranorexDuration.length() - 1
      return "${ranorexDuration.substring(0, end)}.000"
    }
    if (ranorexDuration =~ /^[0-9]*\.[0-9]*s$/) {
      // dealing in seconds
      def end = ranorexDuration.length() - 1
      return (Float.parseFloat(ranorexDuration.substring(0, end)) / 100).toString().substring(0,5)
    }
    if (ranorexDuration =~ /^[0-9]*ms$/) {
      // dealing in milliseconds
      def end = ranorexDuration.length() - 2
      return parseDuration("${(Float.parseFloat(ranorexDuration.substring(0, end)) / 1000)}m")
    }

    throw new IllegalArgumentException("ranorexDuration not understood: ${ranorexDuration}")
  }

  def parseText(ranorexText) {
    ranorexText.replace(' <br /> ', ' ')
               .replace('\n', ' ')
               .replaceAll(~/[ ]{2,}/, ' ')
               .trim()
  }

}