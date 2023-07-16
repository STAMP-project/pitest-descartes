package eu.stamp_project.descartes.operators;

import static eu.stamp_project.test.MethodStreamBuilder.fromClass;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;

import eu.stamp_project.test.input.Calculator;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

class NewInstanceMutationOperatorTest
    extends ParameterlessOperatorTest<NewInstanceMutationOperator> {

  @Override
  NewInstanceMutationOperator buildOperator() {
    return new NewInstanceMutationOperator();
  }

  @Override
  Stream<Method> methodsToMutate() throws NoSuchMethodException {
    return fromClass(Calculator.class).method("getMultipleCalculators", int.class).toStream();
  }

  @Override
  Stream<Method> methodsToAvoid() throws NoSuchMethodException {
    return fromClass(Calculator.class)
        .method("getScreen") // Returning String
        .method("getClone") // Returning class from other packages
        .method("getOptionalCalculator") // Returning class with no public default constructor
        .toStream();
  }

  @Test
  void testReturnEmptyList()
      throws NoSuchMethodException, IllegalAccessException, InstantiationException,
          InvocationTargetException, IOException {
    Method method = Calculator.class.getDeclaredMethod("getMultipleCalculators", int.class);

    List<?> result = (List<?>) invoke(method);
    assertThat(
        "Original result should not be an empty list. Fix the test input.", result, not(empty()));

    Class<?> mutant = mutate(method);
    List<?> resultFromMutant = (List<?>) invoke(mutant, method);

    assertThat("Result should be an empty list", resultFromMutant, empty());
  }
}
