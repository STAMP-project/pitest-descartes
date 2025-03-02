package eu.stamp_project.descartes.reporting.models;

import java.time.Duration;
import java.util.Collection;
import java.util.List;

public class ProjectReport {

  private final Duration duration;
  private final List<ClassReport> findings;
  private final Collection<String> operators;

  public ProjectReport(final Duration duration, final List<ClassReport> findings,
                       final Collection<String> operators) {
    this.duration = duration;
    this.findings = findings;
    this.operators = operators;
  }

  public Duration getDuration() {
    return duration;
  }

  public List<ClassReport> getFindings() {
    return findings;
  }

  public Collection<String> getOperators() {
    return operators;
  }

  public int getTotalNumberOfIssues() {
    return findings.stream()
        .mapToInt(ClassReport::getNumberOfIssues)
        .sum();
  }
}
