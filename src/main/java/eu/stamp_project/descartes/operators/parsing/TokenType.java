package eu.stamp_project.descartes.operators.parsing;

final class TokenType {

  public static final int SYMBOL = (1 << 16);
  public static final int MINUS_SIGN = SYMBOL | 1;
  public static final int LEFT_PARENTHESIS = SYMBOL | 2;
  public static final int RIGHT_PARENTHESIS = SYMBOL | 4;

  public static final int TYPE_IDENTIFIER = (2 << 16);
  public static final int BYTE_TYPE = TYPE_IDENTIFIER | 1;
  public static final int SHORT_TYPE = TYPE_IDENTIFIER | 2;

  public static final int LITERAL = (4 << 16);

  public static final int CHAR_LITERAL = LITERAL | (1 << 12);
  public static final int STRING_LITERAL = LITERAL | (2 << 12);

  public static final int BOOLEAN_LITERAL = LITERAL | (4 << 12);
  public static final int FALSE = BOOLEAN_LITERAL | 1;
  public static final int TRUE = BOOLEAN_LITERAL | 2;

  public static final int NUMERIC_LITERAL = LITERAL | 8 << 12;

  public static final int FLOAT_LITERAL = NUMERIC_LITERAL | (1 << 9);
  public static final int DOUBLE_LITERAL = NUMERIC_LITERAL | (2 << 9);

  public static final int INTEGRAL_LITERAL = NUMERIC_LITERAL | (4 << 9);

  public static final int BYTE_LITERAL = INTEGRAL_LITERAL | (1 << 5);
  public static final int BYTE_BIN_LITERAL = BYTE_LITERAL | 2;
  public static final int BYTE_OCT_LITERAL = BYTE_LITERAL | 8;
  public static final int BYTE_DEC_LITERAL = BYTE_LITERAL | 10;
  public static final int BYTE_HEX_LITERAL = BYTE_LITERAL | 16;

  public static final int SHORT_LITERAL = INTEGRAL_LITERAL | (2 << 5);
  public static final int SHORT_BIN_LITERAL = SHORT_LITERAL | 2;
  public static final int SHORT_OCT_LITERAL = SHORT_LITERAL | 8;
  public static final int SHORT_DEC_LITERAL = SHORT_LITERAL | 10;
  public static final int SHORT_HEX_LITERAL = SHORT_LITERAL | 16;

  public static final int INTEGER_LITERAL = INTEGRAL_LITERAL | (4 << 5);
  public static final int INTEGER_BIN_LITERAL = INTEGER_LITERAL | 2;
  public static final int INTEGER_OCT_LITERAL = INTEGER_LITERAL | 8;
  public static final int INTEGER_DEC_LITERAL = INTEGER_LITERAL | 10;
  public static final int INTEGER_HEX_LITERAL = INTEGER_LITERAL | 16;

  public static final int LONG_LITERAL = INTEGRAL_LITERAL | (8 << 5);
  public static final int LONG_BIN_LITERAL = LONG_LITERAL | 2;
  public static final int LONG_OCT_LITERAL = LONG_LITERAL | 8;
  public static final int LONG_DEC_LITERAL = LONG_LITERAL | 10;
  public static final int LONG_HEX_LITERAL = LONG_LITERAL | 16;

  public static final int END_OF_FILE = 16 << 16;

  private TokenType() {}

  public static boolean is(int token, int category) {
    return (token & category) == category;
  }

  public static int typeToLiteral(int tokenType) {
    if (is(tokenType, SHORT_TYPE)) {
      return SHORT_LITERAL;
    }
    if (is(tokenType, BYTE_TYPE)) {
      return BYTE_LITERAL;
    }
    throw new IllegalArgumentException("Expecting type identifier token types");
  }

  public static Class<?> getNumericType(int tokenType) {
    if (is(tokenType, BYTE_TYPE) || is(tokenType, BYTE_LITERAL)) {
      return Byte.class;
    }
    if (is(tokenType, SHORT_TYPE) || is(tokenType, SHORT_LITERAL)) {
      return Short.class;
    }
    if (is(tokenType, INTEGER_LITERAL)) {
      return Integer.class;
    }
    if (is(tokenType, LONG_LITERAL)) {
      return Long.class;
    }
    if (is(tokenType, FLOAT_LITERAL)) {
      return Float.class;
    }
    if (is(tokenType, DOUBLE_LITERAL)) {
      return Double.class;
    }
    throw new IllegalArgumentException(
        "Token type must represent a numeric literal or a type identifier");
  }

  public static int getRadix(int tokenType) {
    if (!is(tokenType, INTEGRAL_LITERAL)) {
      throw new IllegalArgumentException("Only integral literals have radix");
    }
    return tokenType & 31;
  }
}
