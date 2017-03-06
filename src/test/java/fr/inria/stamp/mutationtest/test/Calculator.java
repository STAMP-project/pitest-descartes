package fr.inria.stamp.mutationtest.test;

/**
 * Meaningless target class for mutation
 */
public class Calculator {


    private double registry;
    private char lastOperatorSymbol;

    public Calculator() {clear();}

    //void method

    public void clear() {
        registry = 0;
        lastOperatorSymbol = '\0';
    }

    //Integral types

    public byte getByte() {
        return (byte)Math.min(Math.abs(registry), 127);
    }

    public short getShort() {
        return (short) Math.min(getCeiling(), 32000);
    }

    public int getCeiling() {
        return (int)Math.ceil(registry);
    }

    public long getSquare() {
        int value = getCeiling();
        return value * value;
    }

    public char getLastOperatorSymbol() {
        return lastOperatorSymbol;
    }

    //Floating point types

    public double add(double input) {
        registry += input;
        lastOperatorSymbol = '+';
        return registry;
    }

    public float getSomething() {
        float value = getCeiling();
        float root = (float)Math.sqrt(2);
        return value * value * root;
    }

    //Boolean

    public boolean isOdd() {
        return getCeiling() % 2 == 0;
    }

    //String

    public String getScreen(int precision) {
        return String.format("%1." + precision, registry);
    }

    // Other reference type

    public Calculator getClone() {
        Calculator result = new Calculator();
        result.registry = registry;
        return result;
    }

}