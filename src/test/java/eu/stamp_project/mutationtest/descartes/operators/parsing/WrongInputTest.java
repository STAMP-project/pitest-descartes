package eu.stamp_project.mutationtest.descartes.operators.parsing;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertTrue;


@RunWith(Parameterized.class)
public class WrongInputTest {

    @Parameters(name = "{index}: {0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                {"vod"},
                {"nothing"},
                {"nil"},
                {"3A"},
                {"B3"},
                {")1"},
                {"-'S'"},
                {"\"a"},
                {"a\""},
                {"(1)2"},
                {"(type)8"},
                {"@"},
                {"(short 3"},
                {"(byte 3"},
                {"(byte) 'A'"},
                {"(short) 'B'"},
                {"''"},
                {"'a"},
                {"'tu'"},
                {"'\\500'"},
                {"1\n2"},
                {"\n"}
                /*{"(byte)10000"}*/ //TODO: This is not failing. Outputs (byte)16
        });
    }

    @Parameter
    public String input;


    @Test
    public void shouldNotParseValue() {
        OperatorParser parser = new OperatorParser(input);
        parser.parse();
        assertTrue(parser.hasErrors());
    }



}