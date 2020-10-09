package eu.stamp_project.descartes.interceptors.stopmethods;

import eu.stamp_project.test.input.Calculator;
import eu.stamp_project.test.input.StopMethods;

import java.lang.reflect.Method;
import java.util.stream.Stream;

import static eu.stamp_project.test.MethodStreamBuilder.fromClass;

class SimpleGetterMatcherTest extends MethodMatcherTest {
    @Override
    Stream<Method> toIntercept() throws NoSuchMethodException {
        return fromClass(StopMethods.class)
                .method("getAByte")
                .method("getAChar")
                .method("getADouble")
                .method("getAFloat")
                .method("getAnInt")
                .method("getALong")
                .method("getAShort")
                .method("getAString")
                .method("getAnObject")
                .method("getStaticField")
                .toStream();
    }

    @Override
    Stream<Method> toAllow() throws NoSuchMethodException {
        return fromClass(Calculator.class)
                .method("getClone")
                .method("getScreen")
                .method("getOptionalCalculator")
                .toStream();
    }

    @Override
    StopMethodMatcher getMatcher() {
        return StopMethodMatchers.isSimpleGetter();
    }
}
