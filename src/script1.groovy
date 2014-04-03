import javax.xml.transform.TransformerFactory
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.stream.StreamSource

def input = new StringBuilder()
new File('docs/sample-report.rxlog').eachLine { line -> input << line }

def xslt = new StringBuilder()
new File('ranorex-junit.xsl').eachLine { line -> xslt << line }

def factory = TransformerFactory.newInstance()
def transformer = factory.newTransformer(new StreamSource(new StringReader(xslt.toString().trim())))
transformer.transform(new StreamSource(new StringReader(input.toString().trim())), new StreamResult(System.out))