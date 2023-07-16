package eu.stamp_project.descartes.reporting.models;

import static eu.stamp_project.descartes.reporting.models.MethodClassification.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.pitest.mutationtest.DetectionStatus.*;

import java.util.Arrays;
import java.util.stream.Stream;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.pitest.classinfo.ClassName;
import org.pitest.mutationtest.DetectionStatus;
import org.pitest.mutationtest.MutationResult;
import org.pitest.mutationtest.MutationStatusTestPair;
import org.pitest.mutationtest.engine.Location;
import org.pitest.mutationtest.engine.MutationDetails;
import org.pitest.mutationtest.engine.MutationIdentifier;

public class MethodRecordTest {

  private static Stream<Arguments> methodRecords() {
    return Stream.of(
        Arguments.of(TESTED, record(KILLED, KILLED, KILLED)),
        Arguments.of(PSEUDO_TESTED, record(SURVIVED, SURVIVED, SURVIVED)),
        Arguments.of(PARTIALLY_TESTED, record(KILLED, SURVIVED, KILLED)),
        Arguments.of(PARTIALLY_TESTED, record(KILLED, SURVIVED, SURVIVED)),
        Arguments.of(PARTIALLY_TESTED, record(SURVIVED, KILLED, KILLED)),
        Arguments.of(PARTIALLY_TESTED, record(SURVIVED, KILLED, SURVIVED)),
        Arguments.of(PARTIALLY_TESTED, record(KILLED, KILLED, SURVIVED)),
        Arguments.of(PARTIALLY_TESTED, record(SURVIVED, SURVIVED, KILLED)),
        Arguments.of(NOT_COVERED, record(NO_COVERAGE, NO_COVERAGE, NO_COVERAGE)));
  }

  @ParameterizedTest
  @MethodSource("methodRecords")
  public void shouldHaveTheRightClassification(
      MethodClassification expectedClassification, MethodRecord record) {
    assertEquals(expectedClassification, record.getClassification());
  }

  @TestFactory
  public Stream<DynamicTest> shouldNotYieldValidRecords() {

    return invalidStatusCombinations()
        .map(
            combination ->
                DynamicTest.dynamicTest(
                    Arrays.toString(combination),
                    () -> assertThrows(IllegalArgumentException.class, () -> record(combination))));
  }

  private static Stream<DetectionStatus[]> invalidStatusCombinations() {
    return Stream.of(
        new DetectionStatus[] {KILLED, KILLED, NO_COVERAGE},
        new DetectionStatus[] {KILLED, NO_COVERAGE, KILLED},
        new DetectionStatus[] {KILLED, NO_COVERAGE, NO_COVERAGE},
        new DetectionStatus[] {KILLED, NO_COVERAGE, SURVIVED},
        new DetectionStatus[] {KILLED, SURVIVED, NO_COVERAGE},
        new DetectionStatus[] {SURVIVED, KILLED, NO_COVERAGE},
        new DetectionStatus[] {SURVIVED, NO_COVERAGE, KILLED},
        new DetectionStatus[] {SURVIVED, NO_COVERAGE, NO_COVERAGE},
        new DetectionStatus[] {SURVIVED, NO_COVERAGE, SURVIVED},
        new DetectionStatus[] {SURVIVED, SURVIVED, NO_COVERAGE},
        new DetectionStatus[] {NO_COVERAGE, KILLED, KILLED},
        new DetectionStatus[] {NO_COVERAGE, KILLED, NO_COVERAGE},
        new DetectionStatus[] {NO_COVERAGE, KILLED, SURVIVED},
        new DetectionStatus[] {NO_COVERAGE, NO_COVERAGE, KILLED},
        new DetectionStatus[] {NO_COVERAGE, NO_COVERAGE, SURVIVED},
        new DetectionStatus[] {NO_COVERAGE, SURVIVED, KILLED},
        new DetectionStatus[] {NO_COVERAGE, SURVIVED, NO_COVERAGE},
        new DetectionStatus[] {NO_COVERAGE, SURVIVED, SURVIVED});
  }

  // Stubs
  public static MutationResult mutation(DetectionStatus status, String mutant) {
    return new MutationResult(
        new MutationDetails(
            new MutationIdentifier(
                new Location(ClassName.fromString("AClass"), "aMethod", "()I"), 1, mutant),
            "path/to/file",
            "Mutation description",
            1,
            0),
        new MutationStatusTestPair(1, status, null));
  }

  public static MutationResult detected(String mutant) {
    return mutation(DetectionStatus.KILLED, mutant);
  }

  public static MutationResult survived(String mutant) {
    return mutation(DetectionStatus.SURVIVED, mutant);
  }

  public static MutationResult notCovered(String mutant) {
    return mutation(DetectionStatus.NO_COVERAGE, mutant);
  }

  public static MethodRecord record(DetectionStatus... statuses) {
    MutationResult[] results = new MutationResult[statuses.length];
    for (int i = 0; i < statuses.length; i++)
      results[i] = mutation(statuses[i], Integer.toString(i));
    return record(results);
  }

  public static MethodRecord record(MutationResult... results) {
    return new MethodRecord(results);
  }
}
