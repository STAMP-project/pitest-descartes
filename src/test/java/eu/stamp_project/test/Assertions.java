package eu.stamp_project.test;

import static org.junit.jupiter.api.Assertions.*;

public class Assertions {

    private Assertions() {}

    private static final double delta = 0.00001;

    public static void assertDifferent(Object unexpected, Object actual) {
        if(unexpected == null) {
            assertNotNull(actual, "Method should not return null");
            return;
        }
        String message = "Original method returned the same value as the mutant should return.";
        if(actual.getClass() == Double.class) {
            assertNotEquals((double)unexpected, (double)actual, delta, message);
        }
        else if(actual.getClass() == Float.class) {
            assertNotEquals((float)unexpected, (float)actual, (float)delta, message);
        }
        assertNotEquals(unexpected, actual, message);
    }

    public static void assertResultEquals(Object expected, Object actual) {
        if(expected == null) {
            assertNull(actual, "Method expected to return null");
            return;
        }
        if(actual.getClass() == Double.class) {
            assertEquals((double)expected, (double)actual, delta);
        }
        else if(actual.getClass() == Float.class) {
            assertEquals((float)expected, (float)actual, (float)delta);
        }
        assertEquals(expected, actual);
    }


}
