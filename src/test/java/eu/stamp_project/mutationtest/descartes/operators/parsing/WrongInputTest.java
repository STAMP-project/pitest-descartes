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
                {"(type)8"},
                /*{"(byte)3"},
                {"(short)3"},
                {"3.0"},
                {"3.0f"},
                {"true"},
                {"false"},
                {"'a'"},
                {"\"string\""},*/

        });
    }

    @Parameter
    public String input;


    @Test
    public void shouldParseValue() {
        OperatorParser parser = new OperatorParser(input);
        parser.parse();
        assertTrue(parser.hasErrors());
    }



}