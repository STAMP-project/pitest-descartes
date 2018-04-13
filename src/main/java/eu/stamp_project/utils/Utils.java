package eu.stamp_project.utils;

import java.util.Arrays;
import java.util.Collection;

public class Utils {

    public static boolean hasFlag(int value, int flag) {
        return (value & flag) == flag;
    }
}
