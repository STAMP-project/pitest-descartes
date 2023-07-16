package eu.stamp_project.test.input;

public class Fluent extends AbstractClass implements Interface {

  public int value = 1;

  public Fluent addOne() {
    value += 1;
    return this;
  }

  public AbstractClass addTwo() {
    value += 2;
    return this;
  }

  public Interface addThree() {
    value += 2;
    return this;
  }

  public static Fluent createNew() {
    return new Fluent().addOne();
  }

  public String getValueAsString() {
    return Integer.toString(value);
  }

  public static String createAndGetValue() {
    return new Fluent().addOne().getValueAsString();
  }

  @Override
  public void abstractMethod() {}

  @Override
  public void voidMethod() {}
}
