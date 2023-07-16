package eu.stamp_project.descartes.reporting.models;

import static eu.stamp_project.descartes.reporting.models.ExtremeMutationResult.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class MethodClassificationTest {

  @Test
  void shouldThrowAnExceptionIfNoMutationIsGiven() {
    assertThrows(IllegalArgumentException.class, () -> {
      MethodClassification.classify(Collections.emptyList());
    }, "Should throw an exception if no mutation is given when obtaining the method classification");
  }

  @Test
  void shouldBeErrorIfAnyErrorIsPresent() {
    assertEquals(
        MethodClassification.classify(List.of(DETECTED, NOT_DETECTED, ERROR)),
        MethodClassification.ERROR,
        "Method classification should be ERROR if there is at least one mutation whose result is ERROR"
    );
  }

  @Test
  void shouldErrorOnMixedCoverageStatus() {
    assertEquals(
        MethodClassification.classify(List.of(DETECTED, NOT_COVERED)),
        MethodClassification.ERROR
    );
  }

  @Test
  void shouldBePartiallyTestedWithMixedDetectionStatus() {
    assertEquals(
        MethodClassification.classify(List.of(DETECTED, NOT_DETECTED, DETECTED)),
        MethodClassification.PARTIALLY_TESTED,
        "Method should be classified as partially tested if there are mixed detected status"
    );
  }

  static Stream<Arguments> provideValuesForUniformClassification() {
    return Stream.of(
        Arguments.of(ERROR, MethodClassification.ERROR),
        Arguments.of(DETECTED, MethodClassification.TESTED),
        Arguments.of(NOT_DETECTED, MethodClassification.PSEUDO_TESTED),
        Arguments.of(NOT_COVERED, MethodClassification.NOT_COVERED)
    );
  }

  @ParameterizedTest(name = "[{index}] all {0} -> {1}")
  @MethodSource("provideValuesForUniformClassification")
  void shouldClassifyAccordingToUniformStatus(ExtremeMutationResult status, MethodClassification expected) {
    assertEquals(
        MethodClassification.classify(List.of(status, status)),
        expected,
        "Method should be classified as " + expected.getName() + " if all mutations are " + status.getName()
    );
  }
}