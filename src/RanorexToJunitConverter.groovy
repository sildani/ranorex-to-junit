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
    def errors = [:]

    testsuite.activity.each { outerTestCase ->
      outerTestCase.activity.each { innerTestCase ->
        innerTestCase.activity.each { module ->
          def key = "${outerTestCase.@testcasename.text()} - ${innerTestCase.@testcasename.text()} - ${module.@modulename.text()}"
          def result = (module.@result.text() == 'Success' ? 'success' : 'fail')
          if (result == 'fail') {
            def errorItem = module.item.findAll { it.@level.text() == 'Error' }[0]
            failures.put(key, [type: module.errmsg.text(), message: errorItem.message.text(), path: errorItem.metainfo.@path.text(), stacktrace: errorItem.metainfo.@stacktrace.text()])
          } else {
            // is there an error item anyway? if so, we're dealing with an ERROR condition (as opposed to a FAILURE, handled above)
            def errorItem = module.item.findAll { it.@level.text() == 'Error' }[0]
            if (errorItem) {
              result = 'error'
              def errorMessages = []
              module.item.findAll { it.@level.text() != 'Error' }.each { item ->
                errorMessages << parseText(item.message.text())
              }
              errors.put(key, [type: errorItem.message.text(), message: errorMessages.join(' | ')])
            }
          }

          testCases.put(key, [name: result, time: parseDuration(module.@duration.text())])
        }
      }
    }

    writer.write '<?xml version="1.0" encoding="UTF-8"?>'
    junit.testsuite(hostname: hostname, name: testsuite.@testsuitename.text(), tests: testCases.size(), failures: failures.size(), errors: errors.size(), time: parseDuration(testsuite.@duration.text()), timestamp: timestamp) {
      properties() {
        testsuite.params.param.each {
          property(name: it.@name.text(), value: it.text())
        }
      }
      testCases.each { classname, testCase ->
        testcase(classname: classname, name: testCase.name, time: testCase.time) {
          if (failures.keySet().contains(classname)) {
            def f = failures.get(classname)
            failure(type: f.type, "Message: ${parseText(f.message)} | Path: ${f.path} | Stacktrace: ${f.stacktrace}")
          }
          if (errors.keySet().contains(classname)) {
            def e = errors.get(classname)
            error(type: e.type, e.message)
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
    def matcher = ranorexDuration =~ /([0-9]*\.?[0-9]+)([a-z]*)/

    if (matcher.matches()) {
      def value = new BigDecimal(matcher[0][1])
      def unit = matcher[0][2]
      def duration

      switch(unit) {
        case "m":
          duration = value * 60
          break
        case "s":
          duration = value
          break
        case "ms":
          duration = value / 1000
          break
      }
      
      return round(duration).toString()
    }

    throw new IllegalArgumentException("ranorexDuration not understood: ${ranorexDuration}")
  }

  def round(value) {
    new BigDecimal(value).setScale(1, BigDecimal.ROUND_HALF_UP)    
  }

  def parseText(ranorexText) {
    ranorexText.replace(' <br /> ', ' ')
               .replace('\n', ' ')
               .replaceAll(~/[ ]{2,}/, ' ')
               .trim()
  }

}