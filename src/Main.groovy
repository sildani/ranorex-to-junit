class Main {
  public static void main(args) {
    if (args.size() != 2) {
      throw new IllegalArgumentException("Must pass two arguments - the relative location of the Ranorex " +
        "test report file and the name of the file itself.")
    }

    def inputFile = new File(args[0] + File.separator + args[1])
    def outputFile = new File(args[0] + File.separator + 'TESTS-TestSuites.xml').withWriter { out ->
      out.write(new RanorexToJunitConverter().convert(inputFile.getText()))
    }

  }
}