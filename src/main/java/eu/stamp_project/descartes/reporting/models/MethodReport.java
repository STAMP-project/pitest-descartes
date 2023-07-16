package eu.stamp_project.descartes.reporting.models;

import static eu.stamp_project.descartes.reporting.models.MethodClassification.ERROR;
import static eu.stamp_project.descartes.reporting.models.MethodClassification.PARTIALLY_TESTED;
import static eu.stamp_project.descartes.reporting.models.MethodClassification.PSEUDO_TESTED;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.pitest.coverage.TestInfo;
import org.pitest.mutationtest.ClassMutationResults;
import org.pitest.mutationtest.MutationResult;

public final class MethodReport {

  private final MethodDetails method;

  private final MethodClassification classification;

  private final Collection<ExtremeMutationReport> mutations;

  private final Set<String> coveringTests;

  public MethodReport(MethodDetails method,
                      Collection<ExtremeMutationReport> mutations,
                      Collection<String> coveringTests) {
    this.method = Objects.requireNonNull(method);
    this.classification = MethodClassification.classify(
        mutations.stream().map(ExtremeMutationReport::getResult));
    this.mutations = new ArrayList<>(mutations);
    this.coveringTests = new HashSet<>(coveringTests);
  }

  public MethodReport(Collection<MutationResult> mutationResults) {
    this(
        new MethodDetails(mutationResults.stream()
            .findFirst()
            .orElseThrow(
                () -> new IllegalArgumentException(
                    "No mutation result found. "
                        + "Method mutation report requires at least one mutation result"))),
        mutationResults.stream().map(ExtremeMutationReport::new).collect(toList()),
        mutationResults.stream().map(
            results -> results.getDetails().getTestsInOrder().stream()
                .map(TestInfo::getName)
                .collect(toList())
        ).flatMap(Collection::stream).collect(toSet())
    );
  }

  @JsonGetter("classification")
  public MethodClassification getClassification() {
    return classification;
  }

  @JsonGetter("tests")
  public Collection<String> getCoveringTests() {
    return Collections.unmodifiableSet(coveringTests);
  }

  @JsonGetter("mutations")
  public Collection<ExtremeMutationReport> getMutations() {
    return Collections.unmodifiableCollection(mutations);
  }

  public Collection<ExtremeMutationReport> getMutationsByResult(ExtremeMutationResult result) {
    return mutations.stream().filter(mutation -> mutation.getResult() == result).collect(toList());
  }

  @JsonIgnore
  public Collection<ExtremeMutationReport> getDetectedMutations() {
    return getMutationsByResult(ExtremeMutationResult.DETECTED);
  }

  @JsonIgnore
  public Collection<ExtremeMutationReport> getNonDetectedMutations() {
    return getMutationsByResult(ExtremeMutationResult.NOT_DETECTED);
  }

  @JsonIgnore
  public Collection<ExtremeMutationReport> getMutationsWithError() {
    return getMutationsByResult(ExtremeMutationResult.ERROR);
  }

  @JsonGetter("details")
  public MethodDetails getMethod() {
    return method;
  }

  public boolean hasIssues() {
    return classification == PSEUDO_TESTED || classification == PARTIALLY_TESTED;
  }

  public boolean hasError() {
    return classification == ERROR;
  }

  public static Stream<MethodReport> getMethodReports(ClassMutationResults results) {
    return results.getMutations().stream()
        .collect(Collectors.groupingBy(MethodDetails::methodKey))
        .values()
        .stream()
        .map(MethodReport::new);
  }
}
