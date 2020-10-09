package eu.stamp_project.descartes.operators.parsing;


class Token {

    public static final Token FALSE = new Token(TokenType.FALSE, "false");
    public static final Token TRUE = new Token(TokenType.TRUE, "true");
    public static final Token BYTE = new Token(TokenType.BYTE_TYPE, "byte");
    public static final Token SHORT = new Token(TokenType.SHORT_TYPE, "short");
    public static final Token LPAR = new Token(TokenType.LEFT_PARENTHESIS, "(");
    public static final Token RPAR = new Token(TokenType.RIGHT_PARENTHESIS, ")");
    public static final Token MINUS = new Token(TokenType.MINUS_SIGN, "-");
    public static final Token EOF = new Token(TokenType.END_OF_FILE, "<END OF INPUT>");

    private final int type;
    private final String lexeme;

    public Token(int type, String lexeme) {
        this.type = type;
        this.lexeme = lexeme;
    }

    public int getType() { return type; }

    public String getLexeme() { return lexeme; }

    public boolean is(int tokenType) { return TokenType.is(type, tokenType);  }

}
