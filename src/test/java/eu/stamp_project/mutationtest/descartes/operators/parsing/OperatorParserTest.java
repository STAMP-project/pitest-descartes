package eu.stamp_project.mutationtest.descartes.operators.parsing;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class OperatorParserTest {

    //TODO: Add more test cases
    @Parameters(name = "{index}: {0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                {"void", Void.class},
                {"null", null},
                {"empty", "empty"},
                {"3", 3},
                {"3L", 3L},
                {"(byte)3", (byte)3},
                {"(short)3", (short)3},
                {"3.0", 3.0},
                {"3.0f", 3.0f},
                {"true", true},
                {"false", false},
                {"'a'", 'a'},
                {"\"string\"", "string"},
                {"-1", -1},
                {"-2L", -2L},
                {"-3.1f", -3.1f},
                {"-4.5", -4.5},
                {"(byte)-5", (byte)-5},
                {"(short)-6", (short)-6},
                {"\"\\n\"", "\n"}
        });
    }

    @Parameter
    public String input;

    @Parameter(1)
    public Object output;

    @Test
    public void shouldParseValue() {
        OperatorParser parser = new OperatorParser(input);
        assertEquals(output, parser.parse());
    }



}