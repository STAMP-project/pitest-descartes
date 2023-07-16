package eu.stamp_project.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.pitest.classinfo.ClassName;
import org.pitest.coverage.TestInfo;
import org.pitest.mutationtest.ClassMutationResults;
import org.pitest.mutationtest.DetectionStatus;
import org.pitest.mutationtest.MutationResult;
import org.pitest.mutationtest.MutationStatusTestPair;
import org.pitest.mutationtest.engine.Location;
import org.pitest.mutationtest.engine.MutationDetails;
import org.pitest.mutationtest.engine.MutationIdentifier;

public class MethodMutationResultsBuilder {

  private static class MutationInfo {
    public String operator;
    public DetectionStatus detectionStatus;
    public String killingTest;

    public MutationInfo(String operator, DetectionStatus detectionStatus, String killingTest) {
      this.operator = Objects.requireNonNull(operator);
      this.detectionStatus = Objects.requireNonNull(detectionStatus);
      this.killingTest = killingTest;
    }
  }

  private ClassName className;

  private String name;
  private String desc;

  private final List<MutationInfo> mutations = new ArrayList<>();

  private List<String> tests = new ArrayList<>();

  public MethodMutationResultsBuilder(String name, String desc) {
    this.name = Objects.requireNonNull(name);
    this.desc = Objects.requireNonNull(desc);
  }

  public MethodMutationResultsBuilder coveredInOrderBy(String... tests) {
    this.tests = List.of(tests);
    return this;
  }

  public MethodMutationResultsBuilder withNonKilledMutation(String operator) {
    return withMutation(operator, DetectionStatus.SURVIVED, null);
  }

  public MethodMutationResultsBuilder withDetectedMutation(String operator, String killingTest) {
    return withMutation(operator, DetectionStatus.KILLED, killingTest);
  }

  public MethodMutationResultsBuilder withNonCoveredMutation(String operator) {
    return withMutation(operator, DetectionStatus.NO_COVERAGE, null);
  }

  public MethodMutationResultsBuilder withMutation(String operator, DetectionStatus status,
                                                   String killingTest) {
    this.mutations.add(new MutationInfo(operator, status, killingTest));
    return this;
  }

  public MethodMutationResultsBuilder inClass(ClassName className) {
    this.className = Objects.requireNonNull(className);
    return this;
  }

  private int getNumberOfTestsRunFor(MutationInfo mutationInfo) {
    if (mutationInfo.killingTest == null) {
      return 0;
    }
    return tests.indexOf(mutationInfo.killingTest);
  }

  private MutationResult toMutationResult(MutationInfo mutationInfo) {
    MutationDetails mutationDetails = new MutationDetails(
        new MutationIdentifier(new Location(className, name, desc), 0, mutationInfo.operator),
        className.asInternalName() + ".java",
        "Mutant description",
        1,
        1
    );
    mutationDetails.addTestsInOrder(tests.stream().map(
        test -> new TestInfo(className.asJavaName(), test, 0, Optional.of(className), 1)
    ).collect(Collectors.toList()));
    return new MutationResult(mutationDetails,
        new MutationStatusTestPair(
            getNumberOfTestsRunFor(mutationInfo),
            mutationInfo.detectionStatus,
            mutationInfo.killingTest)
    );
  }

  public Stream<MutationResult> getMutationResults() {
    return mutations.stream().map(this::toMutationResult);
  }

  public static MethodMutationResultsBuilder method(String desc) {
    return method("method", desc);
  }

  public static MethodMutationResultsBuilder method(String name, String desc) {
    return new MethodMutationResultsBuilder(name, desc);
  }

  public static ClassMutationResults classMutationResults(MethodMutationResultsBuilder... methods) {
    return classMutationResults("example.Class", methods);
  }

  public static ClassMutationResults classMutationResults(String className,
                                                          MethodMutationResultsBuilder... methods) {
    return classMutationResults(ClassName.fromString(className), methods);
  }

  public static ClassMutationResults classMutationResults(ClassName className,
                                                          MethodMutationResultsBuilder... methods) {
    return classMutationResults(Arrays.stream(methods).map(m -> m.inClass(className)));
  }

  private static ClassMutationResults classMutationResults(Stream<MethodMutationResultsBuilder> methods) {
    return new ClassMutationResults(
        methods.flatMap(MethodMutationResultsBuilder::getMutationResults)
            .collect(Collectors.toList())
    );
  }

}
