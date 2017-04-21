package fr.inria.stamp.utils;

import java.util.Arrays;
import java.util.Collection;

public class Utils {

    public static boolean hasFlag(int value, int flag) {
        return (value & flag) == flag;
    }

    public static Collection<Integer> getZeroRange(int start, int end) {
        Integer[] array = new Integer[end - start + 1];
        for (int i=0; i < array.length; i++)
            array[i] = i;
        return Arrays.asList(array);
    }

}
