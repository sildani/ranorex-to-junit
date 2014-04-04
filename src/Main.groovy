class Main {
  public static void main(args) {

    def options = parseArgs(args)

    def inputFile = new File(options.inputFile)
    def outputFile = new File(options.outputFile)

    outputFile.withWriter { out ->
      out.write(new RanorexToJunitConverter().convert(inputFile.getText()))
    }
  }

  private static parseArgs(args) {
    def options = [:]

    def cliOptions = getOptions(args)
    options.inputFile = cliOptions.inputFile
    options.outputFile = cliOptions.outputFile

    options
  }

  private static getOptions(args) {

    def cli = new CliBuilder(usage: 'main <options>')
    cli.with {
      h longOpt: 'help', 'Show usage information'
      i longOpt: 'inputFile', args: 1, argName: 'input_file', required: true, 'Ranorex log input file'
      o longOpt: 'outputFile', args: 1, argName: 'output_file', required: true, 'Output file'
    }

    def cliOptions = cli.parse(args)

    if (cliOptions == null || cliOptions.h) {
      System.exit(2)
    }

    cliOptions
  }

}