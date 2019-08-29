package eu.stamp_project.mutationtest.descartes;

import eu.stamp_project.mutationtest.test.input.NativeMethodClass;
import org.junit.Test;

import java.io.IOException;

import static eu.stamp_project.mutationtest.test.TestUtils.findMutationPoints;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;

public class TestNativeMethods {

    @Test
    public void shouldNotFindNativeMethods() throws IOException  {
        assertThat(findMutationPoints(NativeMethodClass.class), is(empty()));
    }
}
