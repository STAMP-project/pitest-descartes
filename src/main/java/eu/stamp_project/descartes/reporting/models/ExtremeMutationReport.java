package eu.stamp_project.descartes.reporting.models;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Collection;
import java.util.Collections;
import org.pitest.mutationtest.DetectionStatus;
import org.pitest.mutationtest.MutationResult;

public class ExtremeMutationReport {
  private final String operator;

  private final ExtremeMutationResult result;

  private final DetectionStatus detectionStatus;

  private final Collection<String> detectingTests;

  public ExtremeMutationReport(
      final String operator,
      final DetectionStatus detectionStatus,
      final Collection<String> detectingTests
  ) {
    this.operator = operator;
    this.detectionStatus = detectionStatus;
    this.result = ExtremeMutationResult.fromDetectionStatus(detectionStatus);
    this.detectingTests = Collections.unmodifiableCollection(detectingTests);
  }

  public ExtremeMutationReport(MutationResult mutationResult) {
    this(
        mutationResult.getDetails().getMutator(),
        mutationResult.getStatus(),
        mutationResult.getKillingTests());
  }

  @JsonGetter("operator")
  public String getOperator() {
    return operator;
  }

  @JsonIgnore
  public DetectionStatus getDetectionStatus() {
    return detectionStatus;
  }

  @JsonGetter("result")
  public ExtremeMutationResult getResult() {
    return result;
  }

  @JsonGetter("detected-by")
  public Collection<String> getDetectingTests() {
    return detectingTests;
  }

  @JsonIgnore
  public String getDescription() {
    switch (operator) {
      case "empty":
        return "a single instruction returning an empty array";
      case "argument":
        return "a single instruction returning the value of the first argument "
            + "whose type is the same as the return type of the method";
      case "new":
        return "a single instruction returning an empty compatible collection";
      case "void":
        return "an empty body";
      case "optional":
        return "<code>{ return Optional.empty(); }</code>";
      default:
        return "<code>{ return " + operator + "; }</code>";
    }
  }
}
