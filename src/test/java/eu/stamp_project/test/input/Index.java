package eu.stamp_project.test.input;

public enum Index {
  ONE,
  TWO,
  THREE;

  public Index next() {
    switch (this) {
      case ONE:
        return TWO;
      case TWO:
        return THREE;
      default:
        return ONE;
    }
  }
}
