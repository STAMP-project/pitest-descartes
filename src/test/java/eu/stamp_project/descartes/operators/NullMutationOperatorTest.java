package eu.stamp_project.descartes.operators;

import static eu.stamp_project.test.MethodStreamBuilder.fromClass;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import eu.stamp_project.test.input.Calculator;
import java.lang.reflect.Method;
import java.util.stream.Stream;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

class NullMutationOperatorTest extends ParameterlessOperatorTest<NullMutationOperator> {

  @Override
  NullMutationOperator buildOperator() {
    return new NullMutationOperator();
  }

  @Override
  Stream<Method> methodsToMutate() throws NoSuchMethodException {
    return fromClass(Calculator.class)
        .method("getClone")
        .method("getSomeCalculators", int.class)
        .method("getSomeMore", int.class)
        .method("getMultipleCalculators", int.class)
        .method("getRange", int.class)
        .method("getOptionalCalculator")
        .method("getScreen")
        .toStream();
  }

  @Override
  Stream<Method> methodsToAvoid() throws NoSuchMethodException {
    return fromClass(Calculator.class)
        .method("clear")
        .method("getCeiling")
        .method("getByte")
        .method("getShort")
        .method("getCeiling")
        .method("getSquare")
        .method("getRandomOperatorSymbol")
        .method("add", double.class)
        .method("getSomething")
        .method("isOdd")
        .toStream();
  }

  @TestFactory
  Stream<DynamicTest> testMutationReturnsNull() throws NoSuchMethodException {
    return methodsToMutate()
        .map(
            method ->
                DynamicTest.dynamicTest(
                    method.toGenericString(),
                    () -> {
                      assertNotNull(
                          invoke(method),
                          "Original method invocation must not be null, so it is possible to observe the effects of the operator");
                      assertNull(invoke(mutate(method), method), "Mutated method must return null");
                    }));
  }
}
