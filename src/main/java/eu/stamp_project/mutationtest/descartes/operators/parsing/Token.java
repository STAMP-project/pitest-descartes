package eu.stamp_project.mutationtest.descartes.operators.parsing;


public class Token {

    public static final Token FALSE = new Token(TokenType.FALSE_KWD, false);
    public static final Token TRUE = new Token(TokenType.TRUE_KWD, true);
    public static final Token NULL = new Token(TokenType.NULL_KWD, null);
    public static final Token VOID = new Token(TokenType.VOID_KWD, Void.class);
    public static final Token BYTE = new Token(TokenType.BYTE_KWD, "byte");
    public static final Token SHORT = new Token(TokenType.SHORT_KWD, "short");
    public static final Token LPAR = new Token(TokenType.LPAR, "(");
    public static final Token RPAR = new Token(TokenType.RPAR, ")");
    public static final Token MINUS = new Token(TokenType.MINUS, "-");
    public static final Token EOF = new Token(TokenType.EOF, null);
    public static final Token EMPTY = new Token(TokenType.EMPTY_KWD, "empty");
    public static final Token NEW = new Token(TokenType.NEW_KWD, "new");
    public static final Token OPTIONAL = new Token(TokenType.OPTIONAL_KWD, "optional");

    private final TokenType type;
    private final Object data;

    public Token(TokenType type, Object data) {
        this.type = type;
        this.data = data;
    }

    public TokenType getType() { return type; }

    public Object getData() { return data; }

}
