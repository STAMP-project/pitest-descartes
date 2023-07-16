package eu.stamp_project.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public abstract class Converter {

  /**
   * Generic call to valueOf static method of a given numeric type. Ignores underscores in the
   * process.
   *
   * @param type Numeric type to which the given string representation should be converted.
   * @param number String representation of the number.
   * @param <T> A type that extends {@link Number}.
   * @return Returns an instance of the given type resulted from the conversion. If conversion did
   *     not succeed returns null.
   */
  public static <T extends Number> T valueOf(Class<T> type, String number) {
    try {
      return valueOf(type, type.getDeclaredMethod("valueOf", String.class), number);
    } catch (NoSuchMethodException exc) {
      throw new IllegalArgumentException(
          "Given type does not have a valueOf method receiving a String as only parameter",
          exc);
    }
  }

  /**
   * Generic call to valueOf method of given numeric type considering a radix. Ignores underscores
   * in the process.
   *
   * @param type Numeric type to which the given string representation should be converted.
   * @param number String representation of the number.
   * @param radix Radix to use in the conversion.
   * @param <T> A type that extends {@link Number}.
   * @return Returns an instance of the given type resulted form the conversion. If conversion did
   *     not succeed returns null.
   */
  public static <T extends Number> T valueOf(Class<T> type, String number, int radix) {
    try {
      return valueOf(
          type,
          type.getDeclaredMethod("valueOf", String.class, int.class),
          number, radix);
    } catch (NoSuchMethodException exc) {
      throw new IllegalArgumentException(
          "Provided value type does not have a valueOf method receiving"
              + "a String and integer as parameters",
          exc);
    }
  }

  private static <T extends Number> T valueOf(Class<T> type, Method method, Object... args) {
    T result;
    try {
      args[0] = args[0].toString().replace("_", "");
      result = type.cast(method.invoke(null, args));
    } catch (InvocationTargetException exc) {
      // Parsing error
      result = null;
    } catch (Exception exc) {
      // This should not happen
      throw new IllegalStateException(exc.getMessage(), exc);
    }
    return result;
    /* /NOTE:
     *  I have tried to encapsulate the code for obtaining the valueOf method
     *  by getting the type of actual parameters in args.
     *  But when you assign an int value to an Object then it becomes an Integer.
     *  So getDeclaredMethod is not able to find the requested method.
     * */
  }
}
