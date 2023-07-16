package eu.stamp_project.descartes.operators.parsing;

import static eu.stamp_project.descartes.operators.parsing.TokenType.*;
@SuppressWarnings("PMD")
%%

%no_suppress_warnings
%class LiteralLexer

%unicode

%line
%column

%state STRING CHAR


%type Token
%function nextToken

%{

/*  Error handling */

private void error(String message) {
    String fullMessage = String.format("%1$s %1$s at %2$d:%3$d", message, yyline, yycolumn);
    throw new RuntimeException(fullMessage);
}

/* String and char literal */

private StringBuffer string = new StringBuffer();



private String slice() { return slice(0); }

private String clean(String str) { return str.replace("_", ""); }

private Token number(int type, String lexeme) { return new Token(type, clean(lexeme)); }

private Token character(char value) { return new Token(CHAR_LITERAL, Character.toString(value)); }

private Token character() { return character(yycharat(0)); }

private char charFromHex(String str) { return (char)Integer.parseInt(str, 16); }

private char charFromOctal(String str) { return (char)Integer.parseInt(str, 8); }

private String sequence(int start, int length) {
    return yytext().subSequence(start, length).toString();
}

private String sequence(int start) { return sequence(start, yylength()); }

private String slice(int start) { return sequence(start, yylength() - 1); }

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
Digits = \d+
FractionalPart = \. {Digits}
WholeNumber = {Digits} {FractionalPart}
Exponent = [eE] [+-]? {Digits}

DoubleLiteral = (({WholeNumber} | {FractionalPart}) {Exponent}?) | {Digits} {Exponent}
FloatLiteral  = {DoubleLiteral} [fF]

%%
/*Lexical rules*/
<YYINITIAL> {
    false    { return Token.FALSE; }
    true     { return Token.TRUE; }
    "("      { return Token.LPAR; }
    ")"      { return Token.RPAR; }
    byte     { return Token.BYTE; }
    short    { return Token.SHORT; }
    -        { return Token.MINUS; }

    {OctLongLiteral}    { return number(LONG_OCT_LITERAL, slice()); }
    {OctIntegerLiteral} { return number(INTEGER_OCT_LITERAL, yytext()); }

    {DecLongLiteral}    { return number(LONG_DEC_LITERAL, slice()); }
    {DecIntegerLiteral} { return number(INTEGER_DEC_LITERAL, yytext()); }

    {BinLongLiteral}    { return number(LONG_BIN_LITERAL, slice(2)); }
    {BinIntegerLiteral} { return number(INTEGER_BIN_LITERAL, sequence(2));  }

    {HexLongLiteral}    { return number(LONG_HEX_LITERAL, slice(2)); }
    {HexIntegerLiteral} { return number(INTEGER_HEX_LITERAL, sequence(2)); }

    {FloatLiteral}      { return number(FLOAT_LITERAL, slice()); }
    {DoubleLiteral}     { return number(DOUBLE_LITERAL, yytext()); }

    \'  { yybegin(CHAR); }

    \"  { yybegin(STRING); string.setLength(0); }

    [ \b\t\n\r]+ {/* Skip */}
}

<STRING> {
    \"      { yybegin(YYINITIAL); return new Token(STRING_LITERAL, string.toString()); }
    \\b     { string.append('\b'); }
    \\t     { string.append('\t'); }
    \\n     { string.append('\n'); }
    \\f     { string.append('\f'); }
    \\r     { string.append('\r'); }
    \\\\    { string.append('\\'); }
    \\\"    { string.append('"'); }
    \\'     { string.append('\''); }

    \\ ([0-3]? {OctDigit})? {OctDigit} { string.append(charFromOctal(sequence(1))); }
    \\u \p{hex} {4} { string.append(charFromHex(sequence(2))); }
    [^\r\n\"\\]+ { string.append(yytext()); }
}

<CHAR> {
    \\b  \'  { return character('\b'); }
    \\t  \'  { return character('\t'); }
    \\n  \'  { return character('\n'); }
    \\f  \'  { return character('\f'); }
    \\r  \'  { return character('\r'); }
    \\\\ \' { return character('\\'); }
    \\'  \'  { return character('\''); }
    \\ ([1-3]? {OctDigit})? {OctDigit} \'  { return character(charFromOctal(slice(1))); }
    \\u \p{hex} {4} \' { return character(charFromHex(slice(2))); }
    [^\b\t\n\r\\'] \' { return character(); }
}

<<EOF>> { return Token.EOF; }

/* Error fallback */

. { error("Illegal character + <" + yytext() + ">"); }
