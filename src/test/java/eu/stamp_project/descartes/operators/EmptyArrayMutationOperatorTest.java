package eu.stamp_project.descartes.operators;

import eu.stamp_project.test.input.Calculator;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.stream.Stream;

import static eu.stamp_project.test.MethodStreamBuilder.fromClass;
import static org.junit.jupiter.api.Assertions.*;

class EmptyArrayMutationOperatorTest extends ParameterlessOperatorTest<EmptyArrayMutationOperator> {

    @Override
    EmptyArrayMutationOperator buildOperator() {
        return new EmptyArrayMutationOperator();
    }

    @Override
    Stream<Method> methodsToMutate() throws NoSuchMethodException {
        return fromClass(Calculator.class)
                .method("getSomeCalculators", int.class)
                .method("getSomeMore", int.class)
                .method("getRange", int.class)
                .toStream();
    }

    @Override
    Stream<Method> methodsToAvoid() throws NoSuchMethodException {
        return fromClass(Calculator.class)
                .method("clear")
                .method("getByte")
                .method("getShort")
                .method("getCeiling")
                .method("getSquare")
                .method("getRandomOperatorSymbol")
                .method("add", double.class)
                .method("getSomething")
                .method("isOdd")
                .method("getScreen")
                .method("getClone")
                .method("getMultipleCalculators", int.class)
                .method("getOptionalCalculator")
                .toStream();
    }

    @TestFactory
    Stream<DynamicTest> testGeneratesEmptyArray() throws NoSuchMethodException {
        return methodsToMutate().map(
                method -> DynamicTest.dynamicTest(
                        method.toGenericString(),
                        () -> {
                            Object originalResult = invoke(method);
                            assertNotEquals(0, Array.getLength(originalResult));

                            Class<?> mutant = mutate(method, buildOperator());
                            Object mutatedResult = invoke(mutant, method);
                            assertEquals(0, Array.getLength(mutatedResult));
                        }
                )
        );
    }

}
