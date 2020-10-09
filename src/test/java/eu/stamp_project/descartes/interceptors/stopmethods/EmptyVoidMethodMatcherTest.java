package eu.stamp_project.descartes.interceptors.stopmethods;

import eu.stamp_project.test.input.Calculator;
import eu.stamp_project.test.input.StopMethods;

import java.lang.reflect.Method;
import java.util.stream.Stream;

class EmptyVoidMethodMatcherTest extends MethodMatcherTest {

    @Override
    public Stream<Method> toIntercept() throws NoSuchMethodException {
        return Stream.of(StopMethods.class.getDeclaredMethod("emptyVoidMethod"));
    }

    @Override
    Stream<Method> toAllow() throws NoSuchMethodException {
        return Stream.of(Calculator.class.getDeclaredMethod("clear"));
    }

    @Override
    public StopMethodMatcher getMatcher() {
        return StopMethodMatchers.isEmptyVoid();
    }
}
