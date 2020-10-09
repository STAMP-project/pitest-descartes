package eu.stamp_project.descartes.reporting;


import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.io.StringWriter;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class JSONWriterTest {

    public static Stream<Arguments> testData() {
        return Stream.of(
                Arguments.of(
                        "[1,true,\"somestring\",\" escaped \\n \\\" \"]",
                        (Generation) ((JSONWriter json) -> {
                            json.beginList();
                            json.write(1);
                            json.write(true);
                            json.write("somestring");
                            json.write(" escaped \n \" ");
                            json.endList();
                        })
                ),
                Arguments.of(
                        "{\"i\":1,\"b\":true,\"s\":\"string\"}",
                        (Generation) ((JSONWriter json) -> {
                            json.beginObject();
                            json.writeAttribute("i", 1);
                            json.writeAttribute("b", true);
                            json.writeAttribute("s", "string");
                            json.endObject();
                        })
                ),
                Arguments.of(
                        "{\"value\":2,\"left\":{\"value\":1},\"right\":{\"value\":4,\"left\":{\"value\":3},\"right\":{\"value\":5}}}",
                        (Generation) ((JSONWriter json) -> {
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
                        })
                ),
                Arguments.of(
                        "[[1,0,0],[0,1,0],[0,0,1]]",
                        (Generation) ((JSONWriter json) -> {
                            int[][] matrix = {{1, 0, 0}, {0, 1, 0}, {0, 0, 1}};
                            json.beginList();
                            for (int i = 0; i < matrix.length; i++) {
                                json.beginList();
                                for (int j = 0; j < matrix[i].length; j++) {
                                    json.write(matrix[i][j]);
                                }
                                json.endList();
                            }
                            json.endList();
                        })
                ),
                Arguments.of(
                        "[{\"value\":1},{\"value\":2},{\"value\":3}]",
                        (Generation) ((JSONWriter json) -> {
                            json.beginList();
                            for (int i = 0; i < 3; i++) {
                                json.beginObject();
                                json.writeAttribute("value", i + 1);
                                json.endObject();
                            }
                            json.endList();
                        })
                )
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("testData")
    public void testWritesCorrectDocument(String content, Generation action) throws IOException {
        StringWriter writer = new StringWriter();
        JSONWriter json = new JSONWriter(writer);
        action.generate(json);
        String result = writer.getBuffer().toString();
        assertThat(content, is(equalTo(result)));
    }

    interface Generation {
        void generate(JSONWriter json) throws IOException;
    }

}