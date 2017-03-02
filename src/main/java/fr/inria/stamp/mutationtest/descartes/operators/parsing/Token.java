package fr.inria.stamp.mutationtest.descartes.operators.parsing;


public class Token {

    public static final Token FALSE = new Token(TokenType.FALSE_KWD, "false");
    public static final Token TRUE = new Token(TokenType.TRUE_KWD, "true");
    public static final Token NULL = new Token(TokenType.NULL_KWD, "null");
    public static final Token VOID = new Token(TokenType.VOID_KWD, "void");
    public static final Token BYTE = new Token(TokenType.BYTE_KWD, "byte");
    public static final Token SHORT = new Token(TokenType.SHORT_KWD, "short");
    public static final Token LPAR = new Token(TokenType.LPAR, "(");
    public static final Token RPAR = new Token(TokenType.RPAR, ")");
    public static final Token MINUS = new Token(TokenType.MINUS, "-");
    public static final Token EOF = new Token(TokenType.EOF, null);

    private final TokenType type;
    private final String lexeme;


    public Token(TokenType type, String lexeme) {
        this.type = type;
        this.lexeme = lexeme;
    }

    public TokenType getType() { return type; }

    public String getLexeme() { return lexeme; }

}
