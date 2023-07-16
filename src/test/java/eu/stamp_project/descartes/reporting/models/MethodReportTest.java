package eu.stamp_project.descartes.reporting.models;

import static eu.stamp_project.test.MutationResultBuilder.method;
import static eu.stamp_project.test.MutationResultBuilder.classMutationResults;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.both;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Named.named;

import eu.stamp_project.test.MutationResultBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.converter.TypedArgumentConverter;
import org.junit.jupiter.params.provider.MethodSource;
import org.pitest.mutationtest.ClassMutationResults;
import org.pitest.mutationtest.DetectionStatus;

class MethodReportTest {

  static class ToMethodReportConverter extends TypedArgumentConverter<MutationResultBuilder, MethodReport> {
    public ToMethodReportConverter() {
      super(MutationResultBuilder.class, MethodReport.class);
    }

    @Override
    protected MethodReport convert(MutationResultBuilder methodReportTestInput)
        throws ArgumentConversionException {
      return new MethodReport(methodReportTestInput.collect());
    }
  }

  static Stream<Named<MutationResultBuilder>> getPseudoTestedReports() {
    return Stream.of(
      named(
          "Covered method with only one non-detected mutation",

              method("()V").coveredInOrderBy("test1", "test2")
                  .withNonKilledMutation("void")

      ),
      named(
        "Covered method with more than one non-detected mutation",

            method( "()I").coveredInOrderBy("test1")
                .withNonKilledMutation("0")
                .withNonKilledMutation("1")

      )
    );
  }

  @ParameterizedTest
  @MethodSource("getPseudoTestedReports")
  void shouldClassifyMethodAsPseudoTested(@ConvertWith(ToMethodReportConverter.class) MethodReport report) {
    assertEquals(MethodClassification.PSEUDO_TESTED, report.getClassification());
    assertTrue(report.hasIssues());
    assertFalse(report.hasError());
  }
  static Stream<Named<MutationResultBuilder>> getTestedReports() {
    return Stream.of(
        named(
          "Covered method with only one detected mutation",

              method("()V").coveredInOrderBy("test1", "test2")
                  .withDetectedMutation("void", "test1")
        ),
        named(
            "Covered method with more than one detected mutation",
              method("()I").coveredInOrderBy("test1")
                  .withDetectedMutation("0", "test1")
                  .withDetectedMutation("1", "test2")
        ),
        named(
            "Other mutation status",
            method("()I").coveredInOrderBy("test")
                .withMutation("0", DetectionStatus.MEMORY_ERROR, "test")
                .withMutation("0", DetectionStatus.TIMED_OUT, "test")
        )
    );
  }

  @ParameterizedTest
  @MethodSource("getTestedReports")
  void shouldClassifyMethodAsTested(@ConvertWith(ToMethodReportConverter.class) MethodReport report) {
    assertEquals(MethodClassification.TESTED, report.getClassification());
    assertFalse(report.hasIssues());
    assertFalse(report.hasError());
  }

  static Stream<Named<MutationResultBuilder>> getPartiallyTestedReports() {
    return Stream.of(
        named(
            "Method covered by one test with one detected mutation and one non-detected mutation",
            method("()I").coveredInOrderBy("test1")
                .withNonKilledMutation("1")
                .withDetectedMutation("0", "test1")
        ),
        named(
            "Method covered by more than one test with several non-detected mutations and one detected mutation",
            method("()B").coveredInOrderBy("test1", "test2")
                .withNonKilledMutation("254")
                .withDetectedMutation("0", "test2")
                .withNonKilledMutation("1")
            ),
        named(
            "Method covered by one test with several detected mutations and one non-detected mutation",
            method("()B").coveredInOrderBy("test1")
                .withDetectedMutation("0", "test1")
                .withDetectedMutation("1", "test1")
                .withNonKilledMutation("254")
       )
    );
  }

  @ParameterizedTest
  @MethodSource("getPartiallyTestedReports")
  void shouldClassifyMethodAsPartiallyTested(@ConvertWith(ToMethodReportConverter.class) MethodReport report) {
    assertEquals(MethodClassification.PARTIALLY_TESTED, report.getClassification());
    assertTrue(report.hasIssues());
    assertFalse(report.hasError());
  }

  static Stream<Named<MutationResultBuilder>> getNonCoveredReports() {
    return Stream.of(
        named(
            "Non-covered method with one mutation",
            method("()V")
                .withNonCoveredMutation("void")
        ),
        named(
            "Non-covered method with more than one mutation",
            method("()I")
                .withNonCoveredMutation("0")
                .withNonCoveredMutation("1")
        )
    );
  }

  @ParameterizedTest
  @MethodSource("getNonCoveredReports")
  void shouldClassifyMethodAsNotCovered(@ConvertWith(ToMethodReportConverter.class) MethodReport report) {
    assertEquals(MethodClassification.NOT_COVERED, report.getClassification());
    assertFalse(report.hasIssues());
  }

  static Stream<Named<MutationResultBuilder>> getErrorReports() {
    return Stream.of(
        named(
            "Non-covered method with reported covered mutation",
            method("()I")
                .withNonCoveredMutation("1")
                .withNonKilledMutation("0")
        ),
        named(
            "Covered method with reported non-covered mutation",
            method("()I").coveredInOrderBy("test")
                .withDetectedMutation("0", "test")
                .withNonCoveredMutation("1")
        ),
        named(
            "One mutation with error state",
            method("()V").coveredInOrderBy("test")
                .withMutation("void", DetectionStatus.RUN_ERROR, "test")
        ),
        named(
            "One mutation with error state and other detected mutations",
            method("()Z").coveredInOrderBy("test")
                .withMutation("true", DetectionStatus.RUN_ERROR, "test")
                .withDetectedMutation("false","test")
        ),
        named(
            "One mutation error state and other non-detected mutations",
            method("()V").coveredInOrderBy("test1", "test2")
                .withNonKilledMutation("false")
                .withMutation("true", DetectionStatus.NON_VIABLE, "test1")
        )
    );
  }

  @ParameterizedTest
  @MethodSource("getErrorReports")
  void shouldReportError(@ConvertWith(ToMethodReportConverter.class) MethodReport report) {
    assertEquals(MethodClassification.ERROR, report.getClassification());
    assertFalse(report.hasIssues(), "Method report is not expected to include testing issues");
    assertTrue(report.hasError(), "Method report is expected to include an error");
  }

  @Test
  void shouldThrowErrorIfThereAreNoMutants() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new MethodReport(method("()V").collect())
    );
  }

  private static Matcher<MethodReport> hasMethod(String name, String desc) {
    return new TypeSafeDiagnosingMatcher<>() {
      @Override
      public void describeTo(Description description) {
        description
            .appendText("report for ")
            .appendText(name)
            .appendText(desc)
        ;
      }

      @Override
      protected boolean matchesSafely(MethodReport methodReport, Description description) {
        MethodDetails method = methodReport.getMethod();
        return matches(name, method.getMethodName(), "name", description) &&
            matches(desc, method.getMethodDesc(), "description", description);
      }

      private boolean matches(String expected, String actual, String label,
                              Description description) {
        if (!expected.equals(actual)) {
          description
              .appendText("method ")
              .appendText(label)
              .appendText(" was ")
              .appendText(actual)
              .appendText(" instead of ")
              .appendText(expected)
          ;
          return false;
        }
        return true;
      }
    };
  }

  private static Matcher<MethodReport> hasMutations(String... mutationOperators) {
    return new TypeSafeDiagnosingMatcher<>() {
      @Override
      protected boolean matchesSafely(MethodReport methodReport, Description description) {
        Matcher<?> matcher = Matchers.containsInAnyOrder(mutationOperators);
        Collection<String> actualOperators = methodReport.getMutations()
            .stream()
            .map(ExtremeMutationReport::getOperator)
            .collect(Collectors.toList());
        if (!matcher.matches(actualOperators)) {
          matcher.describeMismatch(actualOperators, description);
          return false;
        }
        return true;
      }

      @Override
      public void describeTo(Description description) {
        description.appendValueList("report with mutations {", ",", "}", Arrays.asList(mutationOperators));
      }
    };
  }

  @Test
  void shouldReportOneMethodWithOneMutation()
  {
    ClassMutationResults results = classMutationResults(
        method("()V").coveredInOrderBy("test1", "test2")
            .withNonKilledMutation("void")
    );
    List<MethodReport> reports = MethodReport.getMethodReports(results).collect(Collectors.toList());
    assertThat(reports, hasSize(1));
    assertThat(reports, hasItem(both(hasMethod("method", "()V")).and(hasMutations("void"))));
  }

  @Test
  void shouldGroupMethodMutationResultsIntoMethodReports() {
    ClassMutationResults results = classMutationResults(
          method("()I").coveredInOrderBy("test1", "test2")
              .withNonKilledMutation("1")
              .withDetectedMutation("0", "test1")
    );
    List<MethodReport> reports = MethodReport.getMethodReports(results).collect(Collectors.toList());
    assertThat(reports, hasSize(1));
    assertThat(reports, hasItem(
        both(hasMethod("method", "()I"))
            .and(hasMutations("1", "0"))
    ));
  }

  @Test
  void shouldGroupMutationResults() {
    ClassMutationResults results = classMutationResults(
      method("method1", "()I").coveredInOrderBy("test1", "test2")
          .withNonKilledMutation("42")
          .withDetectedMutation("73", "test2"),
      method("method1", "(I)V").coveredInOrderBy("test2", "test3")
          .withNonKilledMutation("empty"),
      method("method2", "()I").coveredInOrderBy("test4")
          .withDetectedMutation("42", "test4")
          .withDetectedMutation("73", "test4")
    );

    List<MethodReport> reports = MethodReport.getMethodReports(results).collect(Collectors.toList());
    assertThat(reports, hasSize(3));
    assertThat(reports, allOf(
        hasItem(both(hasMethod("method1", "()I")).and(hasMutations("42", "73"))),
        hasItem(both(hasMethod("method1", "(I)V")).and(hasMutations("empty"))),
        hasItem(both(hasMethod("method2", "()I")).and(hasMutations("42", "73")))
    ));

  }
}
