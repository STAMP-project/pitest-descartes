package eu.stamp_project.descartes.interceptors.stopmethods;

import eu.stamp_project.test.input.Calculator;
import eu.stamp_project.test.input.StopMethods;

import java.lang.reflect.Method;
import java.util.stream.Stream;

import static eu.stamp_project.test.MethodStreamBuilder.fromClass;

class HashcodeMethodMatcherTest extends MethodMatcherTest {

    @Override
    Stream<Method> toIntercept() throws NoSuchMethodException {
        return fromClass(StopMethods.class).method("hashCode")
                .toStream();
    }

    @Override
    Stream<Method> toAllow() throws NoSuchMethodException {
        return fromClass(Calculator.class)
                .method("getCeiling") // Same signature
                .method("hashCode", int.class) // Same name
                .toStream();
    }

    @Override
    StopMethodMatcher getMatcher() {
        return StopMethodMatchers.isHashCode();
    }

}
