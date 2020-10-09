package eu.stamp_project.descartes.interceptors;

import eu.stamp_project.test.input.SkipFalseMutationOnAllButOne;
import eu.stamp_project.test.input.SkipMutationOnMethod;
import eu.stamp_project.test.input.SkipMutationTarget;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class SkipDoNotMutateFilterTest extends MutationFilterTest {

    @Override
    public MutationFilter getFilter() { return new SkipDoNotMutateFilter(); }

    @Test
    public void testSkipMethodInAnnotatedClass () throws NoSuchMethodException, IOException {
        Method method = SkipMutationTarget.class.getDeclaredMethod("methodToSkip");
        assertFalse(allows(method, "void"));
    }

    @Test
    public void testSkipAnnotatedMethodInNonAnnotatedClass() throws NoSuchMethodException, IOException {
        Method method = SkipMutationOnMethod.class.getDeclaredMethod("methodToSkip");
        assertFalse(allows(method, "void"));
    }

    @Test
    public void testSkipSpecificMutationFromClassAnnotation() throws NoSuchMethodException, IOException {
        Method method = SkipFalseMutationOnAllButOne.class.getDeclaredMethod("returnFalse");
        assertFalse(allows(method, "false"));
    }

    @Test
    public void testAllowSpecificMutationFromClassAnnotation() throws NoSuchMethodException, IOException {
        Method method = SkipFalseMutationOnAllButOne.class.getDeclaredMethod("returnFalse");
        assertTrue(allows(method, "true"));
    }

    @Test
    public void testSkipSpecificMutationFromMethodAnnotation() throws NoSuchMethodException, IOException {
        Method method = SkipFalseMutationOnAllButOne.class.getDeclaredMethod("returnTrue");
        assertFalse(allows(method, "true"));
    }

    @Test
    public void testAllowSpecificMutationAsPerMethodAnnotation() throws NoSuchMethodException, IOException {
        Method method = SkipFalseMutationOnAllButOne.class.getDeclaredMethod("returnTrue");
        assertTrue(allows(method, "false"));
    }



}