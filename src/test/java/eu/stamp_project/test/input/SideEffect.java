package eu.stamp_project.test.input;

public class SideEffect {

  public int count = 0;

  public void increment() {
    count++;
  }

  public Void nextOdd() {
    count += 1 + count % 2;
    return null;
  }
}
