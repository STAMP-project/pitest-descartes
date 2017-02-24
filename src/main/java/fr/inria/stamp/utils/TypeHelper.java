package fr.inria.stamp.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Utility class to handle the interaction between primitive types and their wrappers
 * Part of the code inspired by/taken from google guava (https://github/google/guava)
 */
public final class TypeHelper {

    private TypeHelper() {}

    private static final Map<Class<?>, Class<?>> PRIMITIVE_TO_WRAPPER_MAP;

    private static final Map<Class<?>,Class<?>> WRAPPER_TO_PRIMITIVE_MAP;

    static {
        PRIMITIVE_TO_WRAPPER_MAP = new HashMap<Class<?>, Class<?>>(16);
        WRAPPER_TO_PRIMITIVE_MAP = new HashMap<Class<?>, Class<?>>(16);

        add(boolean.class, Boolean.class);
        add(byte.class, Byte.class);
        add(char.class, Character.class);
        add(double.class, Double.class);
        add(float.class, Float.class);
        add(int.class, Integer.class);
        add(long.class, Long.class);
        add(short.class, Short.class);
        add(void.class, Void.class);
    }

    private static void add(Class<?> primitive, Class<?> wrapper) {
        PRIMITIVE_TO_WRAPPER_MAP.put(primitive, wrapper);
        WRAPPER_TO_PRIMITIVE_MAP.put(wrapper, primitive);
    }

    public static Set<Class<?>> getPrimitiveTypes() { return PRIMITIVE_TO_WRAPPER_MAP.keySet(); }


    public static Set<Class<?>> getWrapperTypes() { return WRAPPER_TO_PRIMITIVE_MAP.keySet(); }

    public static boolean isPrimitiveType(Class<?> type) {
        //Just for symmetry
        return type.isPrimitive();
    }

    public static boolean isWrapperType(Class<?> type) {
        return WRAPPER_TO_PRIMITIVE_MAP.containsKey(type);
    }

    public static boolean isConstantType(Class<?> type) {
        return  (!type.equals(void.class) && !type.equals(Void.class)) && //Not void
                ((type.isPrimitive() || isWrapperType(type)) || type.equals(String.class));
    }

    public static Class<?> wrap(Class<?> type) {
        if(type.isPrimitive())
            return PRIMITIVE_TO_WRAPPER_MAP.get(type);
        return type;
    }

    public static Class<?> unwrap(Class<?> type) {
        if(isWrapperType(type))
            return WRAPPER_TO_PRIMITIVE_MAP.get(type);
        return type;
    }
}
