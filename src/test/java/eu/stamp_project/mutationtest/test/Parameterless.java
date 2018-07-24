package eu.stamp_project.mutationtest.test;

public class Parameterless {


    public Object getAnObject() {
        return new Exception("Dummy exception");
    }

    public int sumInt() {
        int result = 0;
        for (int i = 0; i < 10; i++, result+=i);
        return result;
    }

    public int[] getVector() {
        int[] result = {1, 0, 0};
        for(int i = 0; i < result.length; i++) {
            result[i] += i;
        }
        return result;
    }

    public int[][] eye() {

        int[][] result = {{0, 0, 0}, {0, 0, 0}, {0, 0, 0}};
        for(int i =0; i < result.length; i++)
            result[i][i] = 1;
        return result;
    }

    public Exception[] getExceptions() {
        return new Exception[]{
                new Exception("one"),
                new Exception("two"),
                new Exception("three"),
        };
    }

    public long sumLong() {
        long result = 0;
        for (int i = 0; i < 10; i++, result+=i);
        return result;
    }

    public double getDouble() {
        return Math.abs(Math.E);
    }

    public float getFloat() {
        float x = .5f;
        for (int i = 0; i < 10; i++, x += 2);
        return x;
    }

    public short workShort() {
        return (short) (System.currentTimeMillis() % 2);
    }

    public byte getAByte() { return (byte)(128 << 2); }

    public boolean isOdd() { return "odd".length() % 2 == 1; }

    public char first() { return "first".charAt(0); }

    //Wrappers
    public Long longWrapper() { return Long.valueOf("1"); }

    public Integer integerWrapper() { return Integer.getInteger("123"); }

    public Short shortWrapper() { return Short.valueOf("1"); }

    public Byte byteWrapper() { return Byte.valueOf("1"); }

    public Double doubleWrapper() { return Double.valueOf("1.0"); }

    public Float floatWrapper() { return Float.valueOf("1f"); }

    public Boolean booleanWrapper() { return Boolean.getBoolean("false"); }

    public Character charWrapper() { return Character.toLowerCase('C'); }

    public String getAString() { return new Integer(123).toString();  }
}
