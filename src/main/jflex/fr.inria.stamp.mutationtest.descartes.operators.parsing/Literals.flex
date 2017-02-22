package fr.inria.stamp.mutationtest.descartes.operators.parsing;

import fr.inria.stamp.utils.Converter;
%%

%class LiteralsLexer

%unicode

%line
%column

%state STRING CHAR

%type Token
%function nextToken

%{

StringBuffer string = new StringBuffer();

private void error(String message, Object... args) {
    throw new RuntimeException(String.format(message, args));
}

private Token charLiteral() {
    return charLiteral(yycharat(0));
}
private Token charLiteral(char value) {
    yybegin(YYINITIAL);
    return new Token(TokenType.CHAR_LITERAL, Character.toString(value));
}

private char fromOctal() {
    return (char)Integer.parseInt(yytext().subSequence(1, yylength()-1).toString(), 8);
}

private char fromHex() {
    return (char)Integer.parseInt(yytext().subSequence(1, yylength()).toString(), 16);
}

/* Numeric literal handling */
private Token getLiteralToken(Object value, TokenType expectedToken) {
    String lexeme = yytext().toString();
    if(value == null) error("Invalid literal value: %0$s at %1$d:%2$d", lexeme, yyline, yycolumn);
    return new Token(expectedToken, value.toString());
}

private Token fromLiteral(Class<? extends Number> type, TokenType expectedToken) {
    return getLiteralToken(Converter.valueOf(type, yytext().toString()), expectedToken);
}

private Token fromLiteral(Class<? extends Number> type, TokenType expectedToken, int radix) {
    return getLiteralToken(Converter.valueOf(type, yytext().toString(), radix), expectedToken);
}

private Token integerLiteral(int radix) {
    return fromLiteral(Integer.class, TokenType.INT_LITERAL, radix);
}

private Token integerLiteral() {
    return fromLiteral(Integer.class, TokenType.INT_LITERAL);
}

private Token longLiteral() {
    return fromLiteral(Long.class, TokenType.LONG_LITERAL);
}

private Token longLiteral(int radix) {
    return fromLiteral(Long.class, TokenType.LONG_LITERAL, radix);
}

private Token floatLiteral() {
    return fromLiteral(Float.class, TokenType.FLOAT_LITERAL);
}

private Token doubleLiteral() {
    return fromLiteral(Double.class, TokenType.DOUBLE_LITERAL);
}


%}

/* Integer literals */
DecIntegerLiteral = 0 | [1-9]([_\d]* \d)?
DecLongLiteral    = {DecIntegerLiteral} [lL]

HexIntegerLiteral = 0 [xX] \p{hex} ([_ \p{hex}]* \p{hex})?
HexLongLiteral    = {HexIntegerLiteral} [lL]

OctIntegerLiteral = 0 [_0-7]* {OctDigit} /*At least two digits to consider it an octal literal and this could disambiguate 0*/
OctLongLiteral    = {OctIntegerLiteral} [lL]
OctDigit = [0-7];

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

    {DecIntegerLiteral} { return integerLiteral(); }
    {DecLongLiteral}    { return longLiteral(); }
    {OctIntegerLiteral} { return integerLiteral(8); }
    {OctLongLiteral}    { return longLiteral(8); }
    {BinIntegerLiteral} {return integerLiteral(2); }
    {BinLongLiteral}    { return longLiteral(2); }
    {FloatLiteral}      { return floatLiteral(); }
    {DoubleLiteral}     { return doubleLiteral(); }

    \'  { yybegin(CHAR); }
    \"  { yybegin(STRING); string.setLength(0); }

    [\b\t\n\r]+ {/* Skip */}
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

[ˆ] { error("Unexpected character at %0$d:%1$d", yyline, yycolumn); }
