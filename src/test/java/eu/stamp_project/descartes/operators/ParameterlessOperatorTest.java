package eu.stamp_project.descartes.operators;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import eu.stamp_project.descartes.codemanipulation.MethodInfo;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

abstract class ParameterlessOperatorTest<T extends MutationOperator> extends MutationOperatorTest {

  abstract T buildOperator();

  @Test
  void testBuildFromIdentifier() {
    T operator = buildOperator();
    MutationOperator operatorFromID = MutationOperator.fromIdentifier(operator.getIdentifier());
    assertThat(operatorFromID, instanceOf(operator.getClass()));
    assertThat(operatorFromID.getIdentifier(), equalTo(operator.getIdentifier()));
    assertThat(operatorFromID.getDescription(), equalTo(operatorFromID.getDescription()));
  }

  abstract Stream<Method> methodsToMutate() throws NoSuchMethodException;

  @TestFactory
  @DisplayName("Should mutate")
  Stream<DynamicTest> testShouldMutateMethods() throws NoSuchMethodException {
    return methodsToMutate()
        .map(
            method ->
                DynamicTest.dynamicTest(
                    method.toGenericString(),
                    () -> assertTrue(buildOperator().canMutate(new MethodInfo(method)))));
  }

  abstract Stream<Method> methodsToAvoid() throws NoSuchMethodException;

  @TestFactory
  @DisplayName("Should not mutate")
  Stream<DynamicTest> testShouldAvoidMethods() throws NoSuchMethodException {
    return methodsToAvoid()
        .map(
            method ->
                DynamicTest.dynamicTest(
                    "Should avoid method " + method.toGenericString(),
                    () -> assertFalse(buildOperator().canMutate(new MethodInfo(method)))));
  }

  Class<?> mutate(Method method) throws IOException {
    return mutate(method, buildOperator());
  }
}
