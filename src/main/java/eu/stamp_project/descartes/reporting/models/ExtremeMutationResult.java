package eu.stamp_project.descartes.reporting.models;

import com.fasterxml.jackson.annotation.JsonValue;
import org.pitest.mutationtest.DetectionStatus;

public enum ExtremeMutationResult {
  DETECTED("detected"),
  NOT_DETECTED("not-detected"),
  NOT_COVERED("not-covered"),
  ERROR("error");

  private final String name;

  ExtremeMutationResult(String name) {
    this.name = name;
  }

  @JsonValue
  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    return getName();
  }

  public static ExtremeMutationResult fromDetectionStatus(DetectionStatus detectionStatus) {
    switch (detectionStatus) {
      case NO_COVERAGE:
        return NOT_COVERED;
      case SURVIVED:
        return NOT_DETECTED;
      case KILLED:
      case TIMED_OUT:
      case MEMORY_ERROR:
        return DETECTED;
      default:
        return ERROR;
    }
  }
}
