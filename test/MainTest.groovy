import org.junit.Test
import org.junit.Ignore
import groovy.mock.interceptor.MockFor

class MainTest {

  @Ignore
  @Test(expected=IllegalArgumentException)
  void should_throw_illegal_argument_exception_if_no_arguments_are_passed() {
    Main.main(new String[0])
  }

  @Ignore
  @Test(expected=IllegalArgumentException)
  void should_throw_illegal_argument_exception_if_only_one_arguments_are_passed() {
    Main.main([ "one arg" ] as String[])
  }

}