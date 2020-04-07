package eu.stamp_project.mutationtest.descartes.operators.parsing;

import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;

public class OperatorParser {

    private final OperatorLexer lexer;

    public OperatorParser(String input) {
        lexer = new OperatorLexer(new StringReader(input));
        errors = new LinkedList<String>();
    }

    private Token lookahead;

    private Object result;

    public Object getResult() {
        return result;
    }

    private final List<String> errors;

    public List<String> getErrors() {
        return errors;
    }

    public boolean hasErrors() {
        return errors.size() > 0;
    }

    private boolean match(TokenType token) throws IOException {
        if (lookahead.getType() == token) {
            try {
                next();
                return true;
            }

            catch (Throwable throwable) {
                errors.add(throwable.getMessage());
                return false;
            }
        }
        return false;
    }

    private void next() throws Throwable {
        lookahead = lexer.nextToken();
    }

    private boolean lookaheadIs(TokenType token) {
        return lookahead.getType() == token;
    }

    private boolean lookaheadIsOneOf(TokenType... tokens) {
        for (int i = 0; i < tokens.length; i++)
            if (lookaheadIs(tokens[i]))
                return true;
        return false;
    }

    public Object parse() {

        result = null;
        try {
            next();
            if (lookaheadIsOneOf(TokenType.NEW_KWD, TokenType.NULL_KWD, TokenType.THIS_KWD, TokenType.VOID_KWD,
                    TokenType.TRUE_KWD, TokenType.FALSE_KWD, TokenType.EMPTY_KWD, TokenType.OPTIONAL_KWD,
                    TokenType.CHAR_LITERAL, TokenType.STRING_LITERAL, TokenType.INT_LITERAL, TokenType.LONG_LITERAL,
                    TokenType.FLOAT_LITERAL, TokenType.DOUBLE_LITERAL)) {
                result = lookahead.getData();
            } else if (lookaheadIs(TokenType.MINUS)) {
                parseNegatedNumber();
            } else if (lookaheadIs(TokenType.LPAR)) {
                parseCastedInteger();
            } else {
                unexpectedTokenError();
            }
            next();
            if (!match(TokenType.EOF))
                unexpectedTokenError();

            lexer.yyclose();

        } catch (Throwable exc) {
            errors.add("Unexpected error: " + exc.getMessage());
            result = null;
        }

        return result;
    }

    // TODO: More descriptive error messages
    private void unexpectedTokenError() {
        errors.add("Unexpected token type: " + lookahead.getType().name());
        result = null;
    }

    private void parseNegatedNumber() throws IOException {
        // TODO: Find a way to refactor this method

        if (match(TokenType.MINUS)) {
            if (lookaheadIs(TokenType.INT_LITERAL)) {
                result = -(Integer) lookahead.getData();
            } else if (lookaheadIs(TokenType.LONG_LITERAL)) {
                result = -(Long) lookahead.getData();
            } else if (lookaheadIs(TokenType.FLOAT_LITERAL)) {
                result = -(Float) lookahead.getData();
            } else if (lookaheadIs(TokenType.DOUBLE_LITERAL)) {
                result = -(Double) lookahead.getData();
            } else
                unexpectedTokenError();
        } else
            unexpectedTokenError();
    }

    private void parseCastedInteger() throws Throwable {
        // TODO: Find a way to refactor this method
        if (match(TokenType.LPAR)) {
            if (lookaheadIs(TokenType.BYTE_KWD)) {
                next();
                if (match(TokenType.RPAR)) {
                    boolean negate = false;
                    if (negate = lookaheadIs(TokenType.MINUS)) {
                        next();
                    }

                    if (lookaheadIs(TokenType.INT_LITERAL)) {
                        result = ((Integer) lookahead.getData()).byteValue();
                        if (negate)
                            result = (byte) (-((Byte) result));
                    } else
                        unexpectedTokenError();
                } else
                    unexpectedTokenError();
            } else if (lookaheadIs(TokenType.SHORT_KWD)) {
                next();
                if (match(TokenType.RPAR)) {
                    boolean negate = false;
                    if (negate = lookaheadIs(TokenType.MINUS)) {
                        next();
                    }

                    if (lookaheadIs(TokenType.INT_LITERAL)) {
                        result = ((Integer) lookahead.getData()).shortValue();
                        if (negate)
                            result = (short) (-(Short) result);
                    } else
                        unexpectedTokenError();
                } else
                    unexpectedTokenError();

            } else
                unexpectedTokenError();
        } else
            unexpectedTokenError();
    }
}
