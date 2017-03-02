package fr.inria.stamp.mutationtest.descartes.operators.parsing;

import java.io.IOException;
import java.io.StringReader;

import static org.junit.Assert.*;
import org.junit.Test;

public class OperatorLexerTest {

    @Test
    public void shouldMatchConstantTokens() {
        TokenType[] types = {
                                TokenType.VOID_KWD,
                                TokenType.NULL_KWD,
                                TokenType.TRUE_KWD,
                                TokenType.FALSE_KWD,
                                TokenType.LPAR,
                                TokenType.BYTE_KWD,
                                TokenType.RPAR,
                                TokenType.SHORT_KWD,
                                TokenType.EOF
                            };
        String input = "void null true false ( byte ) short";
        OperatorLexer lexer = new OperatorLexer(new StringReader(input));

        try {
            for (TokenType token :
                    types) {
                assertEquals("Failed to match: " + token.name(), token, lexer.nextToken().getType());
            }
        }catch(IOException exc){
            fail("Unexpected exception: " + exc.getMessage());
        }
    }

    @Test
    public void shouldMatchIntegerLiterals() {
        String input = "1 0b1101 0b1_0_0 0B1 0XFF 0xA_a 0111 07_1_1";
        int[] values = {1, 13, 4, 1, 255, 170, 73, 457};
        OperatorLexer lexer = new OperatorLexer(new StringReader(input));
        try{
            for(int i=0; i < values.length; i++) {
                Token token = lexer.nextToken();
                assertEquals(token.getType(), TokenType.INT_LITERAL);
                assertEquals(Integer.parseInt(token.getLexeme()), values[i]);
            }
        }catch(IOException exc) {
            fail("Unexpected exception: " + exc.getMessage());
        }
    }

    @Test
    public void shouldMatchLongLiterals() {
        String input = "12_345_678_910L  0133767016076l 0x2DFDC1C3El";
        long value = 12345678910L;
        OperatorLexer lexer = new OperatorLexer(new StringReader(input));
        try {
            Token lookahed = lexer.nextToken();
            while (lookahed.getType() != TokenType.EOF) {
                assertEquals(lookahed.getType(), TokenType.LONG_LITERAL);
                assertEquals(Long.parseLong(lookahed.getLexeme()), value);
                lookahed = lexer.nextToken();
            }
        }catch(IOException exc) {
            fail("Unexpected exception: " + exc.getMessage());
        }
    }

    @Test
    public void shouldMatchStringLiterals() {
        String[] inputs = {
                "\"This is a string\"",
                "\"\\123\"",
                "\"\\uFFAA\""
        };
        String[] values = {
                "This is a string",
                "\123",
                "\uFFAA"
        };
        for(int i=0; i<inputs.length; i++) {

            try {
                OperatorLexer lexer = new OperatorLexer(new StringReader(inputs[i]));
                Token token = lexer.nextToken();
                assertEquals(token.getType(), TokenType.STRING_LITERAL);
                assertEquals(token.getLexeme(), values[i]);
            } catch (IOException exc) {
                fail("Unexcpected expection: " + exc.getMessage());
            }
        }

    }



}