package eu.stamp_project.mutationtest.test;

public class StopMethods {

    static {
        System.out.println("Static initializer");
    }

    public void emptyVoidMethod() {}

    public boolean returnTrue () { return true; }

    public byte returnByte() { return (byte)11; }

    public short returnShort() { return (short)12; }

    public int returnNegativeInt() { return -11; }

    public int returnPositiveInt() { return  11; }

    public int returnOne() { return 1; }

    public long returnPositiveLong() { return 1234567890L; }

    public long returnNegativeLong() { return -1234567890L; }

    public float returnPositiveFloat() { return 3.14f; }

    public float returnNegativeFloat() { return -3.14f; }

    public double returnPositiveDouble() { return 6.28; }

    public double returnNegativeDouble() { return -6.28; }

    public char returnChar() { return 'A'; }

    public String returnString() {  return "A"; }

    public Object returnNull() { return null; }


    public int[] emptyIntArray() {
        return new int[0];

    }

    //The actually produces the same bytecode for this method as the code produced for the previous version
    public int[] emptyInt2Array() {
        return new int[]{};
    }

    public int[][] emptyMatrix() { return new int[0][]; }

    public String[] emptyStringArray() { return new String[0]; }

    public String[][] emptyStringMatrix() { return new String[0][]; }


    boolean aBoolean;
    byte aByte;
    short aShort;
    int anInt;
    long aLong;
    float aFloat;
    double aDouble;
    char aChar;
    String aString;
    Object anObject;

    public void setaBoolean(boolean aBoolean) {
        this.aBoolean = aBoolean;
    }

    public void setaByte(byte aByte) {
        this.aByte = aByte;
    }

    public void setaChar(char aChar) {
        this.aChar = aChar;
    }

    public void setaDouble(double aDouble) {
        this.aDouble = aDouble;
    }

    public void setaFloat(float aFloat) {
        this.aFloat = aFloat;
    }

    public void setaLong(long aLong) {
        this.aLong = aLong;
    }

    public void setAnInt(int anInt) {
        this.anInt = anInt;
    }

    public void setaShort(short aShort) {
        this.aShort = aShort;
    }

    public void setaString(String aString) {
        this.aString = aString;
    }

    public void setAnObject(Object anObject) {
        this.anObject = anObject;
    }

    public byte getaByte() {
        return aByte;
    }

    public char getaChar() {
        return aChar;
    }

    public double getaDouble() {
        return aDouble;
    }

    public float getaFloat() {
        return aFloat;
    }

    public int getAnInt() {
        return anInt;
    }

    public long getaLong() {
        return aLong;
    }

    public short getaShort() {
        return aShort;
    }

    public String getaString() {
        return aString;
    }

    public Object getAnObject() {
        return anObject;
    }

    public StopMethods setFluent(int value) {
        this.anInt = value;
        return this;
    }

    public StopMethods onlyReturnsThis() { return this; }

    public int onlyReturnsAPrimitiveParameter(int a, int b, int c) { return b;  }

    public String onlyReturnsAReferenceParameter(int a, int b, String c) { return c; }

    @Deprecated
    public boolean isDeprecated() { return  Boolean.valueOf("tr " + "ue"); }

    static int staticField;

    public int getStaticField() { return staticField; }

    public void setStaticField(int value) { staticField = value; }

    @Override
    public int hashCode() { return "1234".length() << 2; }

    @Override
    public String toString() { return this.getClass().getName(); }

    public void delegateSelfStaticVoid(int value) {
        this.setStaticField(value);
    }

    public void delegateSelfVoid(int value) {
        this.setAnInt(value);
    }

    public String delegateSelfValue(String that, int start, int end) {
        return this.delegateParameterValue(that, start, end);
    }

    public String delegateParameterValue(String that, int start, int end) {
        return that.substring(start, end);
    }

    public String delegateFieldValue(int start, int end) {
        return aString.substring(start, end);
    }

    public static void delegateToExternalClass(String message) {
        System.out.println(message);
    }

    public static void emptyStatic() {}

    public void delegateToStatic (String message) {
        delegateToExternalClass(message);
    }

    public void delegateToStatic() { emptyStatic(); }

}
