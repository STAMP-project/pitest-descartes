package eu.stamp_project.mutationtest.descartes.reporting;

public enum MethodClassification {
    TESTED("tested"),
    PSEUDO_TESTED("pseudo-tested"),
    PARTIALLY_TESTED("partially-tested"),
    NOT_COVERED("not-covered");

    MethodClassification(String name) {
        this.name = name;
    }

    private String name;

    public String toString() {
        return name;
    }

}
