package fr.inria.stamp.mutationtest.descartes.reporting;

import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.pitest.functional.SideEffect1;

import static org.junit.Assert.*;

abstract class JSONTestCase {

    String expected;

    public JSONTestCase(String expected) {
        this.expected = expected;
    }

    public abstract void generate(JSONWriter json) throws IOException;

    public void execute() {
        StringWriter writer = new StringWriter();
        JSONWriter json = new JSONWriter(writer);
        try {
           generate(json);
        }catch(IOException exc) {
            fail("Unexpected exception: " + exc.getMessage());
        }
        assertEquals(expected, writer.getBuffer().toString());
    }

}

@RunWith(Parameterized.class)
public class JSONWriterTest {
    //Bizarre way of testing (o_O) but I wanted to refactor the testing code and treat code fragments as data
    //Also a nice place to use a grammar based test case generator

    @Parameters(name = "{index}: {0}")
    public static Collection<Object[]> getCases() {
        return Arrays.asList(new Object[][] {
            {
                "Simple list",
                new JSONTestCase(
                        "[1,true,\"somestring\",\" escaped \\n \\\" \"]") {
                    @Override
                    public void generate(JSONWriter json) throws IOException {
                        json.beginList();
                        json.write(1);
                        json.write(true);
                        json.write("somestring");
                        json.write(" escaped \n \" ");
                        json.endList();
                    }
                }
            },
            {
                "Simple object",
                new JSONTestCase("{\"i\":1,\"b\":true,\"s\":\"string\"}") {
                    @Override
                    public void generate(JSONWriter json) throws IOException {
                        json.beginObject();
                        json.writeAttribute("i", 1);
                        json.writeAttribute("b", true);
                        json.writeAttribute("s", "string");
                        json.endObject();
                    }
                }
            },

            {
                "Binary tree",// {"value":2,"right":{"value":1},"left":{"value":4,"left":{"value":3},"right":{"value":5}}}
                new JSONTestCase("{\"value\":2,\"left\":{\"value\":1},\"right\":{\"value\":4,\"left\":{\"value\":3},\"right\":{\"value\":5}}}") {
                    @Override
                    public void generate(JSONWriter json) throws IOException {
                        json.beginObject();
                            json.writeAttribute("value", 2);

                            json.beginObjectAttribute("left");
                                json.writeAttribute("value", 1);
                            json.endObject();

                            json.beginObjectAttribute("right");
                                json.writeAttribute("value", 4);

                                json.beginObjectAttribute("left");
                                    json.writeAttribute("value", 3);
                                json.endObject();

                                json.beginObjectAttribute("right");
                                    json.writeAttribute("value", 5);
                                json.endObject();
                            json.endObject();

                        json.endObject();
                    }
                }
            },
            {
                "Matrix",
                new JSONTestCase("[[1,0,0],[0,1,0],[0,0,1]]") {
                    @Override
                    public void generate(JSONWriter json) throws IOException {
                        int[][] matrix  = {{1, 0, 0}, {0, 1, 0}, {0, 0, 1}};
                        json.beginList();
                        for(int i=0; i < matrix.length; i++) {
                            json.beginList();
                            for (int j = 0; j < matrix[i].length; j++) {
                                json.write(matrix[i][j]);
                            }
                            json.endList();
                        }
                        json.endList();
                    }
                }
            },
            {
                "List of objects",//[{"value":1},{"value":2},{"value":3}]
                new JSONTestCase("[{\"value\":1},{\"value\":2},{\"value\":3}]") {
                    @Override
                    public void generate(JSONWriter json) throws IOException {

                        json.beginList();
                        for(int i=0; i < 3; i++) {
                            json.beginObject();
                            json.writeAttribute("value", i+1);
                            json.endObject();
                        }
                        json.endList();
                    }
                }
            },

        });
    }

    @Parameter
    public String description;

    @Parameter(1)
    public JSONTestCase testCase;

  @Test
    public void shouldRunCases() {
        testCase.execute();
    }

}