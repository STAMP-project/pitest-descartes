package eu.stamp_project.test;

import eu.stamp_project.utils.TypeHelper;

import java.util.Arrays;

public class TestValues {

    private static boolean DEFAULT_BOOL;
    private static char DEFAULT_CHAR;
    private static byte DEFAULT_BYTE;
    private static short DEFAULT_SHORT;
    private static int DEFAULT_INT = 10;
    private static long DEFAULT_LONG;
    private static float DEFAULT_FLOAT;
    private static double DEFAULT_DOUBLE;
    private static String DEFAULT_STRING = "default";

    public static Object defaultFor(Class<?> type) {
        Class<?> target = TypeHelper.wrap(type);
        if (target == Boolean.class)
            return DEFAULT_BOOL;
        if (target == Character.class)
            return DEFAULT_CHAR;
        if (target == Byte.class)
            return DEFAULT_BYTE;
        if(target == Short.class)
            return DEFAULT_SHORT;
        if (target == Integer.class)
            return DEFAULT_INT;
        if (target == Long.class)
            return DEFAULT_LONG;
        if (target == Float.class)
            return DEFAULT_FLOAT;
        if (target == Double.class)
            return DEFAULT_DOUBLE;
        if(target == String.class)
            return DEFAULT_STRING;
        return null;
    }

    public static Object[] defaultsFor(Class<?>[] types) {
        return Arrays.stream(types).map(TestValues::defaultFor).toArray(Object[]::new);
    }
}
