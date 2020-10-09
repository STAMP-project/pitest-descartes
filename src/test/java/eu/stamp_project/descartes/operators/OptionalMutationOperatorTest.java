package eu.stamp_project.descartes.operators;

import eu.stamp_project.test.input.Calculator;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.stream.Stream;

import static eu.stamp_project.test.MethodStreamBuilder.fromClass;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertFalse;

class OptionalMutationOperatorTest extends ParameterlessOperatorTest<OptionalMutationOperator> {

    @Override
    OptionalMutationOperator buildOperator() {
        return new OptionalMutationOperator();
    }

    private Method getMethod() throws NoSuchMethodException {
        return Calculator.class.getDeclaredMethod("getOptionalCalculator");
    }

    @Override
    Stream<Method> methodsToMutate() throws NoSuchMethodException {
        return Stream.of(getMethod());
    }

    @Override
    Stream<Method> methodsToAvoid() throws NoSuchMethodException {
        return fromClass(Calculator.class).except("getOptionalCalculator").toStream();
    }

    @Test
    void testReturnsEmptyOptional() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, IOException {
        checkOriginalValueIsNotEmpty();
        checkMutatedMethodReturnsEmptyOptional();
    }

    private void checkOriginalValueIsNotEmpty() {
        Calculator calc = new Calculator();
        Optional<Calculator> originalValue = calc.getOptionalCalculator();
        assertFalse(originalValue.isEmpty(), "Method getOptionalCalculator in Calculator class must not return an empty Optional");
    }

    private void checkMutatedMethodReturnsEmptyOptional() throws NoSuchMethodException, IOException, IllegalAccessException, InstantiationException, InvocationTargetException {
        Method method = getMethod();
        Class<?> mutant = mutate(method);
        Object result = invoke(mutant, method);
        assertThat("Mutated method must return the empty Optional", result, equalTo(Optional.empty()));
    }

}
