package eu.stamp_project.descartes.operators;

import static eu.stamp_project.test.Assertions.assertDifferent;
import static eu.stamp_project.test.Assertions.assertResultEquals;
import static eu.stamp_project.test.MethodStreamBuilder.fromClass;
import static eu.stamp_project.test.TestValues.defaultFor;

import eu.stamp_project.test.input.Calculator;
import java.lang.reflect.Method;
import java.util.stream.Stream;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

class ArgumentPropagationOperatorTest
    extends ParameterlessOperatorTest<ArgumentPropagationOperator> {

  @Override
  ArgumentPropagationOperator buildOperator() {
    return new ArgumentPropagationOperator();
  }

  @Override
  Stream<Method> methodsToMutate() throws NoSuchMethodException {
    return fromClass(Calculator.class)
        .method("add", double.class)
        .method("toString", String.class)
        .method("hashCode", int.class)
        .toStream();
  }

  @Override
  Stream<Method> methodsToAvoid() throws NoSuchMethodException {
    return fromClass(Calculator.class)
        .allDeclaredMethods()
        .except("add", double.class)
        .except("toString", String.class)
        .except("hashCode", int.class)
        .toStream();
  }

  @TestFactory
  Stream<DynamicTest> testMutation() throws NoSuchMethodException {
    return methodsToMutate()
        .map(
            method ->
                DynamicTest.dynamicTest(
                    method.toGenericString(),
                    () -> {
                      Object defaultValue = defaultFor(method.getReturnType());
                      Object value = invoke(method);
                      assertDifferent(defaultValue, value);
                      Class<?> mutant = mutate(method);
                      Object mutatedResult = invoke(mutant, method);
                      assertResultEquals(defaultValue, mutatedResult);
                    }));
  }
}
