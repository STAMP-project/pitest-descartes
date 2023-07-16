package eu.stamp_project.test;

import static org.junit.jupiter.api.Assertions.*;

public final class Assertions {

  private Assertions() {}

  private static final double DELTA = 0.00001;

  public static void assertDifferent(Object unexpected, Object actual) {
    if (unexpected == null) {
      assertNotNull(actual, "Method should not return null");
      return;
    }
    String message = "Original method returned the same value as the mutant should return.";
    if (actual.getClass() == Double.class) {
      assertNotEquals((double) unexpected, (double) actual, DELTA, message);
    } else if (actual.getClass() == Float.class) {
      assertNotEquals((float) unexpected, (float) actual, (float) DELTA, message);
    }
    assertNotEquals(unexpected, actual, message);
  }

  public static void assertResultEquals(Object expected, Object actual) {
    if (expected == null) {
      assertNull(actual, "Method expected to return null");
      return;
    }
    if (actual.getClass() == Double.class) {
      assertEquals((double) expected, (double) actual, DELTA);
    } else if (actual.getClass() == Float.class) {
      assertEquals((float) expected, (float) actual, (float) DELTA);
    }
    assertEquals(expected, actual);
  }
}
