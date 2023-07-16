package eu.stamp_project.test;

import eu.stamp_project.utils.TypeHelper;
import java.util.Arrays;

public class TestValues {

  private static final boolean DEFAULT_BOOL = false;
  private static final char DEFAULT_CHAR = '\0';
  private static final byte DEFAULT_BYTE = 0;
  private static final short DEFAULT_SHORT = 0;
  private static final int DEFAULT_INT = 10;
  private static final long DEFAULT_LONG = 0L;
  private static final float DEFAULT_FLOAT = 0.0f;
  private static final double DEFAULT_DOUBLE = 0.0;
  private static final String DEFAULT_STRING = "default";

  public static Object defaultFor(Class<?> type) {
    Class<?> target = TypeHelper.wrap(type);
    if (target == Boolean.class) {
      return DEFAULT_BOOL;
    }
    if (target == Character.class) {
      return DEFAULT_CHAR;
    }
    if (target == Byte.class) {
      return DEFAULT_BYTE;
    }
    if (target == Short.class) {
      return DEFAULT_SHORT;
    }
    if (target == Integer.class) {
      return DEFAULT_INT;
    }
    if (target == Long.class) {
      return DEFAULT_LONG;
    }
    if (target == Float.class) {
      return DEFAULT_FLOAT;
    }
    if (target == Double.class) {
      return DEFAULT_DOUBLE;
    }
    if (target == String.class) {
      return DEFAULT_STRING;
    }
    return null;
  }

  public static Object[] defaultsFor(Class<?>[] types) {
    return Arrays.stream(types).map(TestValues::defaultFor).toArray(Object[]::new);
  }
}
