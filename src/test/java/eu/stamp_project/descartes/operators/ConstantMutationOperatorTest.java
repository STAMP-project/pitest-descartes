package eu.stamp_project.descartes.operators;

import eu.stamp_project.descartes.codemanipulation.MethodInfo;
import eu.stamp_project.test.input.Calculator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.stream.Stream;

import static eu.stamp_project.test.Assertions.assertDifferent;
import static eu.stamp_project.test.Assertions.assertResultEquals;
import static org.junit.jupiter.api.Assertions.*;


class ConstantMutationOperatorTest  extends MutationOperatorTest {

    // In this test class we use MutationOperator.fromID to instantiate the operator, as the mutation requires a valid ID.

    private static Stream<Arguments> methodsToMutate() throws NoSuchMethodException {
        return Stream.of(
          Arguments.of(Calculator.class.getDeclaredMethod("getByte"), "(byte)3"),
                Arguments.of(Calculator.class.getDeclaredMethod("getShort"), "(short)3"),
                Arguments.of(Calculator.class.getDeclaredMethod("getCeiling"), "3"),
                Arguments.of(Calculator.class.getDeclaredMethod("getWrapper"), "3"),
                Arguments.of(Calculator.class.getDeclaredMethod("getSquare"), "3L"),
                Arguments.of(Calculator.class.getDeclaredMethod("getRandomOperatorSymbol"), "'A'"),
                Arguments.of(Calculator.class.getDeclaredMethod("getSomething"), "3.3f"),
                Arguments.of(Calculator.class.getDeclaredMethod("getScreen"), "\"Literal\""),
                Arguments.of(Calculator.class.getDeclaredMethod("isOdd"), "false"),
                Arguments.of(Calculator.class.getDeclaredMethod("add", double.class), "3.3")
        );
    }

    @ParameterizedTest(name = "{1} for {0}")
    @MethodSource("methodsToMutate")
    void testCanMutate(Method method, String identifier) {
        ConstantMutationOperator operator = (ConstantMutationOperator)MutationOperator.fromID(identifier);
        assertTrue(operator.canMutate(new MethodInfo(method)));
    }

    @ParameterizedTest(name = "{1} for {0}")
    @MethodSource("methodsToMutate")
    void testMutantReturnsRightValue(Method method, String identifier) throws IOException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        ConstantMutationOperator operator = (ConstantMutationOperator)MutationOperator.fromID(identifier);
        Class<?> mutant = mutate(method, operator);
        Object originalResult = invoke(method);
        assertDifferent(operator.getConstant(), originalResult);
        Object resultFromMutant = invoke(mutant, method);
        assertResultEquals(operator.getConstant(), resultFromMutant);
    }

    private static Stream<Arguments> invalidMutations() throws NoSuchMethodException {
        return Stream.of(
                Arguments.of(Calculator.class.getDeclaredMethod("getByte"), "3"),
                Arguments.of(Calculator.class.getDeclaredMethod("getShort"), "(byte)3"),
                Arguments.of(Calculator.class.getDeclaredMethod("getCeiling"), "(short)3"),
                Arguments.of(Calculator.class.getDeclaredMethod("getSquare"), "true"),
                Arguments.of(Calculator.class.getDeclaredMethod("getWrapper"), "3.3"),
                Arguments.of(Calculator.class.getDeclaredMethod("getRandomOperatorSymbol"), "\"A\""),
                Arguments.of(Calculator.class.getDeclaredMethod("getSomething"), "3.3"),
                Arguments.of(Calculator.class.getDeclaredMethod("getScreen"), "'A'"),
                Arguments.of(Calculator.class.getDeclaredMethod("isOdd"), "1"),
                Arguments.of(Calculator.class.getDeclaredMethod("add", double.class), "3")
        );
    }

    @ParameterizedTest(name = "Can not use {1} to mutate {0}")
    @MethodSource("invalidMutations")
    void testCanNotMutate(Method method, String identifier) {
        ConstantMutationOperator operator = (ConstantMutationOperator)MutationOperator.fromID(identifier);
        assertFalse(operator.canMutate(new MethodInfo(method)));
    }

    @Test
    void testInvalidConstant() {
        assertThrows(IllegalArgumentException.class, () -> new ConstantMutationOperator("", Optional.of(2)));
    }

    @Test
    void testNull() {
        assertThrows(NullPointerException.class, () -> new ConstantMutationOperator("null", null));
    }

}
