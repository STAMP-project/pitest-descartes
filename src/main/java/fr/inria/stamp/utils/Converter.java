
package fr.inria.stamp.utils;

import java.lang.Number;
import java.lang.reflect.Method;


public abstract class Converter {

    /**
     * Generic call to valueOf static method of given numeric type. Ignores underscores in the process.
     * @param type Numeric type to which the given string representation should be converted.
     * @param number String representation of the number.
     * @param <T> A type that extends {@link Number}.
     * @return Returns an instance of the given type resulted from the conversion. If conversion did not succeed returns null.
     */
    public static <T extends Number> T valueOf(Class<T> type, String number) {
        return valueOf(type, new Object[]{ number });

    }

    /**
     * Generic call to valueOf method of given numeric type considering a radix. Ignores underscores in the process.
     * @param type Numeric type to which the given string representation should be converted.
     * @param number String representation of the number.
     * @param radix Radix to use in the conversion.
     * @param <T> A type that extends {@link Number}.
     * @return Returns an instance of the given type resulted form the conversion. If conversion did not succedd returns null.
     */
    public static <T extends Number> T valueOf(Class<T> type, String number, int radix) {
        return valueOf(type, new Object[] { number,  radix });
    }

    private static <T extends Number> T valueOf(Class<T> type, Object... args) {
        T result;
        try {
            args[0] = args[0].toString().replace("_", "");
            Method method = type.getDeclaredMethod("valueOf", getTypes(args));
            result = type.cast(method.invoke(args));
        }
        catch(Exception exc) { //If for whatever reason the number could not be parsed return null
            result = null;
        }
        return result;

    }

    private static Class<?>[] getTypes(Object... items) {
        if(items == null) return null;
        Class<?>[] result = new Class<?>[items.length];
        for(int i=0; i < items.length; i++)
            result[i] = items[i].getClass();
        return result;
    }

}
