package eu.stamp_project.descartes.reporting.models;

public enum MethodClassification {
  TESTED("tested"),
  PSEUDO_TESTED("pseudo-tested"),
  PARTIALLY_TESTED("partially-tested"),
  NOT_COVERED("not-covered");

  MethodClassification(String name) {
    this.name = name;
  }

  private final String name;

  public String toString() {
    return name;
  }

  public String getName() {
    return name;
  } // Making it compatible with Velocity
}
