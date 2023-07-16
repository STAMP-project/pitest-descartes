package eu.stamp_project.descartes.reporting.models;

import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum MethodClassification {
  ERROR("error"),
  TESTED("tested"),
  PSEUDO_TESTED("pseudo-tested"),
  PARTIALLY_TESTED("partially-tested"),
  NOT_COVERED("not-covered");

  MethodClassification(String name) {
    this.name = name;
  }

  private final String name;

  @Override
  public String toString() {
    return name;
  }

  @JsonValue
  public String getName() {
    return name;
  }

  public static MethodClassification classify(Stream<ExtremeMutationResult> mutationResults) {
    Set<ExtremeMutationResult> outcome = mutationResults.collect(Collectors.toSet());
    if (outcome.isEmpty()) {
      throw new IllegalArgumentException(
          "Can not classify an empty collection of extreme mutation results");
    }
    if (outcome.size() > 1) {
      // ERROR: If there is at least one error status or,
      // if there are non-covered and other different status at the same time.
      if (outcome.contains(ExtremeMutationResult.NOT_COVERED)
          || outcome.contains(ExtremeMutationResult.ERROR)) {
        // Not covered should be the only result
        return ERROR;
      }
      // Has detected and not detected transformations at the same time
      return PARTIALLY_TESTED;
    }
    return classify(outcome.stream().findFirst().orElse(ExtremeMutationResult.ERROR));
  }

  public static MethodClassification classify(Collection<ExtremeMutationResult> mutationResults) {
    return classify(Objects.requireNonNull(mutationResults).stream());
  }

  private static MethodClassification classify(ExtremeMutationResult extremeMutationResult) {
    switch (extremeMutationResult) {
      case DETECTED:
        return TESTED;
      case NOT_DETECTED:
        return PSEUDO_TESTED;
      case NOT_COVERED:
        return NOT_COVERED;
      default:
        return ERROR;
    }
  }
}
