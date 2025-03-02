package eu.stamp_project.descartes.operators.parsing;

import static eu.stamp_project.descartes.operators.parsing.TokenType.BOOLEAN_LITERAL;
import static eu.stamp_project.descartes.operators.parsing.TokenType.CHAR_LITERAL;
import static eu.stamp_project.descartes.operators.parsing.TokenType.END_OF_FILE;
import static eu.stamp_project.descartes.operators.parsing.TokenType.INTEGRAL_LITERAL;
import static eu.stamp_project.descartes.operators.parsing.TokenType.LEFT_PARENTHESIS;
import static eu.stamp_project.descartes.operators.parsing.TokenType.MINUS_SIGN;
import static eu.stamp_project.descartes.operators.parsing.TokenType.NUMERIC_LITERAL;
import static eu.stamp_project.descartes.operators.parsing.TokenType.RIGHT_PARENTHESIS;
import static eu.stamp_project.descartes.operators.parsing.TokenType.STRING_LITERAL;
import static eu.stamp_project.descartes.operators.parsing.TokenType.TYPE_IDENTIFIER;
import static eu.stamp_project.descartes.operators.parsing.TokenType.getNumericType;
import static eu.stamp_project.descartes.operators.parsing.TokenType.getRadix;
import static eu.stamp_project.descartes.operators.parsing.TokenType.typeToLiteral;
import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.NoSuchElementException;

public class LiteralParser {

  public static class Result {

    private final Object value;
    private final String error;

    private Result(Object value, String error) {
      this.value = value;
      this.error = error;
    }

    public static Result ok(Object value) {
      requireNonNull(value, "Value can not be null in successful parsing result");
      return new Result(value, null);
    }

    public static Result error(String error) {
      if (isBlankOrNull(error)) {
        throw new IllegalArgumentException(
            "Resulting error message can not be null, empty or blank.");
      }
      return new Result(null, error);
    }

    public boolean hasError() {
      return error != null;
    }

    public Object getValue() {
      if (hasError()) {
        throw new NoSuchElementException("Parsing resulted in error. No value was produced.");
      }
      return value;
    }

    public String getError() {
      return hasError() ? error : null;
    }
  }

  private Token lookahead;

  private LiteralLexer lexer;

  public Result parse(String line) {
    if (isBlankOrNull(line)) {
      return Result.error("Input is null or blank");
    }
    try {
      lexer = new LiteralLexer(new StringReader(line));
      Result result = null;
      next();
      if (lookahead.is(CHAR_LITERAL)) {
        result = Result.ok(lookahead.getLexeme().charAt(0));
      } else if (lookahead.is(STRING_LITERAL)) {
        result = Result.ok(lookahead.getLexeme());
      } else if (lookahead.is(BOOLEAN_LITERAL)) {
        result = Result.ok(Boolean.valueOf(lookahead.getLexeme()));
      } else if (lookahead.is(NUMERIC_LITERAL)) {
        result = Result.ok(tokenToNumber(lookahead));
      } else if (lookahead.is(MINUS_SIGN)) {
        result = negativeNumericLiteral();
      } else if (lookahead.is(LEFT_PARENTHESIS)) {
        result = byteOrShortLiteral();
      }
      if (result == null) {
        return Result.error("Invalid literal notation");
      }
      if (!result.hasError()) {
        next();
        if (!lookahead.is(END_OF_FILE)) {
          result = Result.error("End of line expected.");
        }
      }
      return result;
    } catch (Exception exc) {
      return Result.error("Unexpected exception " + exc);
    }
  }

  private Result negativeNumericLiteral() throws IOException {
    next();
    if (lookahead.is(NUMERIC_LITERAL)) {
      return Result.ok(tokenToNumber(new Token(lookahead.getType(), "-" + lookahead.getLexeme())));
    }
    return Result.error("Expecting numeric literal after minus sign.");
  }

  @SuppressWarnings("checkstyle:variabledeclarationusagedistance")
  private Result byteOrShortLiteral() throws IOException {
    next();
    if (!lookahead.is(TYPE_IDENTIFIER)) {
      return Result.error("Expecting type names byte or short, got: " + lookahead.getLexeme());
    }
    int resultType = lookahead.getType();
    String prefix = "";
    next();
    if (!lookahead.is(RIGHT_PARENTHESIS)) {
      return Result.error("Expecting right parenthesis, got: " + lookahead.getLexeme());
    }
    next();
    if (lookahead.is(MINUS_SIGN)) {
      prefix = "-";
      next();
    }
    if (!lookahead.is(INTEGRAL_LITERAL)) {
      return Result.error("Expecting an integral literal, got: " + lookahead.getLexeme());
    }

    return Result.ok(
        tokenToNumber(
            new Token(
                typeToLiteral(resultType) | getRadix(lookahead.getType()),
                prefix + lookahead.getLexeme())));
  }

  public void next() throws IOException {
    lookahead = lexer.nextToken();
  }

  private Object tokenToNumber(Token token) {
    Class<?> numericType = getNumericType(token.getType());
    if (token.is(INTEGRAL_LITERAL)) {
      return integerValueOf(numericType, token.getLexeme(), getRadix(token.getType()));
    }
    return valueOf(numericType, token.getLexeme());
  }

  private Object valueOf(Class<?> type, String value) {
    try {
      Method method = type.getDeclaredMethod("valueOf", String.class);
      return method.invoke(null, value);
    } catch (NoSuchMethodException | IllegalAccessException exc) {
      throw new AssertionError(
          "Class " + type.getName()
              + " expected to have an accessible valueOf(String) method", exc);
    } catch (InvocationTargetException exc) {
      throw new IllegalArgumentException(
          "Error while obtaining value of type " + type.getName() + " from string " + value, exc);
    }
  }

  private Object integerValueOf(Class<?> type, String value, int radix) {
    try {
      Method method = type.getDeclaredMethod("valueOf", String.class, int.class);
      return method.invoke(null, value, radix);
    } catch (NoSuchMethodException | IllegalAccessException exc) {
      throw new AssertionError(
          "Class "
              + type.getName()
              + " expected to have an accessible valueOf(String, int) method", exc);
    } catch (InvocationTargetException exc) {
      throw new IllegalArgumentException(
          "Error while obtaining value of type "
              + type.getName()
              + " from string "
              + value
              + " using radix "
              + radix,
          exc);
    }
  }

  private static boolean isBlankOrNull(CharSequence sequence) {
    if (sequence == null || sequence.length() == 0) {
      return true;
    }
    int length = sequence.length();
    if (length == 0) {
      return true;
    }
    for (int index = 0; index < length; index++) {
      if (!Character.isWhitespace(sequence.charAt(index))) {
        return false;
      }
    }
    return true;
  }
}
