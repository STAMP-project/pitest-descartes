package eu.stamp_project.utils;

public class Utils {

    public static boolean hasFlag(int value, int flag) {
        return (value & flag) == flag;
    }

    public static boolean isConstructor(String name) {  return name.equals("<init>"); }
}
