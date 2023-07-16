package eu.stamp_project.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
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

public class MutationResultBuilder {
  public String name;
  public String desc;
  public List<String> tests = new ArrayList<>();

  private final List<MutationResult> mutations = new ArrayList<>();

  public MutationResultBuilder(String name, String desc) {
    this.name = name;
    this.desc = desc;
  }

  public MutationResultBuilder coveredInOrderBy(String... tests) {
    this.tests = List.of(tests);
    return this;
  }

  public MutationResultBuilder withNonKilledMutation(String operator) {
    return withMutation(operator, DetectionStatus.SURVIVED, null);
  }

  public MutationResultBuilder withDetectedMutation(String operator, String killingTest) {
    return withMutation(operator, DetectionStatus.KILLED, killingTest);
  }

  public MutationResultBuilder withNonCoveredMutation(String operator) {
    return withMutation(operator, DetectionStatus.NO_COVERAGE, null);
  }

  public MutationResultBuilder withMutation(String operator, DetectionStatus status, String killingTest) {
    this.mutations.add(createMutation(operator, status, killingTest));
    return this;
  }

  private MutationResult createMutation(String operator, DetectionStatus status, String killingTest) {
    MutationDetails mutationDetails = new MutationDetails(
        new MutationIdentifier(
            new Location(ClassName.fromString("example.Class"), name, desc), 0, operator
        ),
        "example/Class.java",
        "Description",
        1,
        1
    );
    mutationDetails.addTestsInOrder(tests.stream().map(
        test -> new TestInfo(
            "example/ClassTest",
            test,
            0,
            Optional.of(ClassName.fromString("example/Class")),
            1
        )
    ).collect(Collectors.toList()));
    return new MutationResult(mutationDetails,
        new MutationStatusTestPair((killingTest == null)?0: tests.indexOf(killingTest), status, killingTest)
    );
  }

  public Collection<MutationResult> collect() {
    return mutations;
  }

  public Stream<MutationResult> collectAsStream() {
    return mutations.stream();
  }

  public static MutationResultBuilder method(String name, String desc) {
    return new MutationResultBuilder(name, desc);
  }

  public static MutationResultBuilder method(String desc) {
    return method("method", desc);
  }

  public static ClassMutationResults classMutationResults(MutationResultBuilder... reports) {
    return new ClassMutationResults(
        Arrays.stream(reports).flatMap(MutationResultBuilder::collectAsStream).collect(Collectors.toList())
    );
  }

}
