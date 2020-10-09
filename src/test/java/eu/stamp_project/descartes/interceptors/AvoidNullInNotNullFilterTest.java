
package eu.stamp_project.descartes.interceptors;

import org.junit.jupiter.api.Test;
import org.pitest.mutationtest.engine.MutationDetails;
import tests.Person;

import java.io.IOException;
import java.lang.reflect.Method;

import static eu.stamp_project.test.Utils.getClassTree;
import static eu.stamp_project.test.Utils.toMutationIdentifier;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AvoidNullInNotNullFilterTest extends MutationFilterTest<AvoidNullInNotNullFilter> {

    //NOTE: Not using parameterized tests to make explicit the intention of each test case.

    @Override
    public AvoidNullInNotNullFilter getFilter() { return new AvoidNullInNotNullFilter(); }

    private Method method(String name, Class<?>... params) throws NoSuchMethodException {
        return tests.PersonKt.class.getDeclaredMethod(name, params);
    }

    @Test
    public void testNullOperatorNonNullResult() throws IOException, NoSuchMethodException {
        assertFalse(allows(method("getNonNullString"), "null"));
    }

    @Test
    public void testNullOperatorPotentialNullResult() throws NoSuchMethodException, IOException {
        assertTrue(allows(method("getPossiblyNullString", Person.class), "null"));
    }

    @Test
    public void testNonNullOperatorNonNullResult() throws NoSuchMethodException, IOException {
        assertTrue(allows(method("getNonNullString"), "\"A\""));
    }

    @Test
    public void testNonNullOperatorPotentiallyNulResult() throws NoSuchMethodException, IOException {
        assertTrue(allows(method("getPossiblyNullString", Person.class), "\"A\""));
    }
}