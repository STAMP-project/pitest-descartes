package eu.stamp_project.descartes.operators.parsing;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class LiteralParserTest {

  @DisplayName("Should parse valid literal specifications")
  @ParameterizedTest(name = "Input: {0}")
  @MethodSource
  void testParseLiteral(String input, Object expected) {
    LiteralParser parser = new LiteralParser();
    LiteralParser.Result result = parser.parse(input);
    assertFalse(result.hasError(), "Parsing should succeed. Got error: " + result.getError());
    assertThat(result.getValue(), equalTo(expected));
  }

  @SuppressWarnings("PMD.UnusedPrivateMethod")
  private static Stream<Arguments> testParseLiteral() {
    return Stream.of(
        Arguments.of("1", 1),
        Arguments.of("-1", -1),
        Arguments.of("1.2f", 1.2f),
        Arguments.of("1.0", 1.0),
        Arguments.of("(short)-1", (short) -1),
        Arguments.of("(short)3", (short) 3),
        Arguments.of("(byte)1", (byte) 1),
        Arguments.of("(byte)-2", (byte) -2),
        Arguments.of("123L", 123L),
        Arguments.of("true", true),
        Arguments.of("'\\n'", '\n'),
        Arguments.of("\"\"", ""),
        Arguments.of("\"A\"", "A"),
        Arguments.of("0b1101", 0b1101),
        Arguments.of("0b1_0_0", 0b1_0_0),
        Arguments.of("0B1", 0B1),
        Arguments.of("0XFF", 0XFF),
        Arguments.of("0xA_a", 0xA_a),
        Arguments.of("0111", 0111),
        Arguments.of("07_1_1", 07_1_1),
        Arguments.of("-2L", -2L),
        Arguments.of("-4.5", -4.5),
        Arguments.of("12_345_678_910L", 12_345_678_910L),
        Arguments.of("0133767016076l", 0133767016076l),
        Arguments.of("0x2DFDC1C3El", 0x2DFDC1C3El),
        Arguments.of("\"\\123\"", "\123"),
        Arguments.of("\"\\uFFAA\"", "\uFFAA"),
        Arguments.of("'a'", 'a'),
        Arguments.of("'\\r'", '\r'),
        Arguments.of("'\\n'", '\n'),
        Arguments.of("'\\t'", '\t'),
        Arguments.of("'\\b'", '\b'),
        Arguments.of("'\\f'", '\f'),
        Arguments.of("'\\''", '\''),
        Arguments.of("'\\\\'", '\\'),
        Arguments.of("'\\7'", '\7'),
        Arguments.of("'\\67'", '\67'),
        Arguments.of("'\\354'", '\354'),
        Arguments.of("'\\u1234'", '\u1234'));
  }

  @DisplayName("Should not parse invalid literal specifications")
  @ParameterizedTest(name = "Input: {0}")
  @MethodSource
  void testShouldNotParseWrongInput(String input) {
    LiteralParser parser = new LiteralParser();
    LiteralParser.Result result = parser.parse(input);

    assertTrue(result.hasError(), "Accepted wrong input.");
  }

  @SuppressWarnings("PMD.UnusedPrivateMethod")
  private static Stream<String> testShouldNotParseWrongInput() {
    return Stream.of(
        "1 2",
        "-",
        "",
        "(",
        "(short)true",
        "@",
        "vod",
        "nothing",
        "nil",
        "3A",
        "B3",
        ")1",
        "-'S'",
        "\"a",
        "a\"",
        "(1)2",
        "(type)8",
        "(short 3",
        "(byte 3",
        "(byte) 'A'",
        "(short) 'B'",
        "''",
        "'a",
        "'tu'",
        "'\\500'");
  }
}
