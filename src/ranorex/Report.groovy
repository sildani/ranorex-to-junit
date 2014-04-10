package ranorex

import groovy.xml.MarkupBuilder

public class Report {

  def host
  def timestamp
  def duration
  def result
  def suites = []

  public Report(String xml) {
    parse(xml)
  }

  public String toJunit() {

    def writer = new StringWriter()
    def builder = new MarkupBuilder(writer)

    writer.write('<?xml version="1.0" encoding="UTF-8"?>')

    suites.each { suite ->
      def failures = suite.testCases.sum { it.failedcount }
      def errors = suite.testCases.sum { it.errorcount }
      def total = suites.testCases.size()

      builder.testsuite(hostname: 'hostname', name: suite.name, tests: total, failures: failures, errors: errors, time: duration, timestamp: timestamp) {
        suite.testCases.each { test ->
          testcase(classname:suite.name, name:test.name, time:test.duration) {
            test.failures.each { f ->
              failure(type:f.type, f.message.text().trim())
            }
          }
        }
      }
    }
    
    writer.toString()
  }

  private void parse(String input) {
    def slurper = new XmlSlurper()
    def xml = slurper.parseText(input)

    def ts = xml.activity.find { it.@user != null }
    this.host = ts.@host.text()
    this.result = ts.@result.text()
    this.timestamp = parseTimestamp(ts.@timestamp.text())

    xml.activity.activity.findAll { a -> a.@testsuitename != null }.each { suite ->

      def newSuite = [name:suite.@testsuitename, duration:parseDuration(suite.@duration), testCases:[]]

      dig(suite, []).each { test ->
        def testcase = [:]
        testcase.name = test.@testcasename.text()
        testcase.result = test.@result.text()
        testcase.duration = parseDuration(test.@duration.text())
        testcase.type = test.@type.text()
        testcase.errorcount = test.@totalerrorcount.text()
        testcase.warningcount = test.@totalwarningcount.text()
        testcase.successcount = test.@totalsuccesscount.text()
        testcase.failedcount = test.@totalfailedcount.text()
        testcase.blockedcount = test.@totalblockedcount.text()
        
        testcase.failures = []

        test.activity.findAll { it.@modulename.text().size() && it.@result == 'Failed' }.each { failure ->
          def type = failure.@moduletype
          def message = failure.item.find { it.@level.text() == 'Failure' }.message

          testcase.failures.add([type:type, message:message])
        }

        newSuite.testCases.add(testcase)
      }

      suites.add(newSuite)
    }

    this.duration = suites.sum { s -> s.duration }
  }

  private dig(node, nodes) {
    def childNodes = node.activity.findAll { it.@testcasename.text().size() }

    if (childNodes.size() == 0) 
      nodes << node
    else
      childNodes.each { dig(it, nodes) }
    
    return nodes
  }

  private String parseTimestamp(input) {
    def dt = Date.parse('M/d/yyyy h:m:s a', input)
    dt.format('yyyy-MM-dd') + "T" + dt.format('HH:mm:ss')
  }

  private String parseDuration(inputDuration) {
    def matcher = inputDuration =~ /([0-9]*\.?[0-9]+)([a-z]*)/

    if (matcher.matches()) {
      def value = new BigDecimal(matcher[0][1])
      def unit = matcher[0][2]
      def duration

      switch(unit) {
        case "h":
          duration = value * 3600
          break
        case "m":
          duration = value * 60
          break
        case "ms":
          duration = value / 1000
          break
        default:
          duration = value
          break
      }
      
      return round(duration).toString()
    }

    throw new IllegalArgumentException("inputDuration not understood: ${inputDuration}")
  }

  private BigDecimal round(value) {
    new BigDecimal(value).setScale(1, BigDecimal.ROUND_HALF_UP)    
  }

}