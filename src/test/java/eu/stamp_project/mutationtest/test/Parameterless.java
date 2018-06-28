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

}
