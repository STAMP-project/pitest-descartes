package fr.inria.stamp.mutationtest.descartes.operators.parsing;

import java.util.LinkedHashMap;
import fr.inria.stamp.utils.Converter;
%%

%class OperatorLexer

%unicode

%line
%column

%state STRING CHAR


%type Token
%function nextToken

%{

private static enum Literal {

    INT(TokenType.INT_LITERAL, Integer.class, 0),
    LONG(TokenType.LONG_LITERAL, Long.class, 1),
    FLOAT(TokenType.FLOAT_LITERAL, Float.class, 1),
    DOUBLE(TokenType.DOUBLE_LITERAL, Double.class, 0);

    TokenType token;
    Class<? extends Number> type;
    int end;

    Literal(TokenType token, Class<? extends Number> type, int end) {
        this.token = token;
        this.type = type;
        this.end = end;
    }
}

private static enum Base {

    BINARY(2, 2),
    OCTAL(8, 1),
    DECIMAL(10, 0),
    HEXADECIMAL(16, 2);

    int ten;
    int start;

    Base(int ten, int start) {
        this.ten = ten;
        this.start = start;
    }
}

/*  Error handling */

private void error(String message) {
    String fullMessage = String.format("%1$s %1$s at %2$d:%3$d", message, yyline, yycolumn);
    throw new RuntimeException(fullMessage);
}

/* String and char literal */

private StringBuffer string = new StringBuffer();

private Token charLiteral() {
    return charLiteral(yycharat(0));
}

private Token charLiteral(char value) {
    yybegin(YYINITIAL);
    return new Token(TokenType.CHAR_LITERAL, value);
}

private char fromOctal() {
    return (char)Integer.parseInt(yytext().subSequence(1, yylength()).toString(), 8);
}

private char fromHex() {
    return (char)Integer.parseInt(yytext().subSequence(2, yylength()).toString(), 16);
}

/* Numeric literal handling */

private Token parse(Literal literal, Base system) {
    String lexeme = new String(zzBuffer, zzStartRead + system.start, yylength() - (system.start + literal.end));
    Object value = Converter.valueOf(literal.type, lexeme, system.ten);
    return tokenOrError(value, literal.token, lexeme);
}

private Token parse(Literal literal) {
    String lexeme = new String(zzBuffer, zzStartRead, yylength() - literal.end);
    Object value = Converter. valueOf(literal.type, lexeme);
    return tokenOrError(value, literal.token, lexeme);
}

private Token tokenOrError(Object value, TokenType token, String lexeme) {
    if(value == null) error("Invalid literal: " + lexeme);
    return new Token(token, value);
}

%}

/* Integer literals */
DecIntegerLiteral = 0 | [1-9]([_\d]* \d)?
DecLongLiteral    = {DecIntegerLiteral} [lL]

HexIntegerLiteral = 0 [xX] \p{hex} ([_ \p{hex}]* \p{hex})?
HexLongLiteral    = {HexIntegerLiteral} [lL]

OctIntegerLiteral = 0 [_0-7]* {OctDigit} /*At least two digits to consider it an octal literal and this could disambiguate 0*/
OctLongLiteral    = {OctIntegerLiteral} [lL]
OctDigit = [0-7]

BinIntegerLiteral = 0 [bB] [01] ([_01]* [01])?
BinLongLiteral    = {BinIntegerLiteral} [lL]

/* Floating point literals */
FloatLiteral  = ({FLit1}|{FLit2}|{FLit3}) {Exponent}? [fF]
DoubleLiteral = ({FLit1}|{FLit2}|{FLit3}) {Exponent}?

FLit1    = \d+ \. \d*
FLit2    = \. \d+
FLit3    = \d+
Exponent = [eE] [+-]? \d+

%%
/*Lexical rules*/
<YYINITIAL> {
    null  { return Token.NULL; }
    void  { return Token.VOID; }
    false { return Token.FALSE; }
    true  { return  Token.TRUE; }
    "("   { return Token.LPAR; }
    ")"   { return Token.RPAR; }
    byte  { return Token.BYTE; }
    short { return Token.SHORT; }
    -     {return Token.MINUS; }

    {OctLongLiteral}    { return parse(Literal.LONG, Base.OCTAL); }
    {OctIntegerLiteral} { return parse(Literal.INT, Base.OCTAL); }

    {DecLongLiteral}    { return parse(Literal.LONG); }
    {DecIntegerLiteral} { return parse(Literal.INT); }


    {BinLongLiteral}    { return parse(Literal.LONG, Base.BINARY); }
    {BinIntegerLiteral} { return parse(Literal.INT, Base.BINARY);  }

    {HexLongLiteral}    { return parse(Literal.LONG, Base.HEXADECIMAL); }
    {HexIntegerLiteral} { return parse(Literal.INT, Base.HEXADECIMAL); }

    {FloatLiteral}      { return parse(Literal.FLOAT); }
    {DoubleLiteral}     { return parse(Literal.DOUBLE); }

    \'  { yybegin(CHAR); }
    \"  { yybegin(STRING); string.setLength(0); }

    [ \b\t\n\r]+ {/* Skip */}
}

<STRING> {
    \"      { yybegin(YYINITIAL); return new Token(TokenType.STRING_LITERAL, string.toString()); }
    \\b     { string.append('\b'); }
    \\t     { string.append('\t'); }
    \\n     { string.append('\n'); }
    \\f     { string.append('\f'); }
    \\r     { string.append('\r'); }
    \\\\    { string.append('\\'); }
    \\\"    { string.append('"'); }
    \\\'    { string.append('\''); }

    \\[0-3]? {OctDigit}? {OctDigit} { string.append(fromOctal()); }
    \\u \p{hex} {4} { string.append(fromHex()); }
    [^\r\n\"\\]+ { string.append(yytext()); }
}

<CHAR> {
    \\b\'  { return charLiteral(); }
    \\t\'  { return charLiteral(); }
    \\n\'  { return charLiteral(); }
    \\f\'  { return charLiteral(); }
    \\r\'  { return charLiteral(); }
    \\\\\' { return charLiteral(); }
    \\\'\' { return charLiteral(); }
    \\ ([1-3]? {OctDigit})? {OctDigit} \' { return charLiteral(fromOctal()); }
    \\u \p{hex} {4} \' { return charLiteral(fromHex()); }
    [^\r\n\'\\] { return charLiteral(yytext().charAt(0)); }
}

<<EOF>> { return Token.EOF; }

/* Error fallback */

[ˆ] { error("Unexpected character"); }
