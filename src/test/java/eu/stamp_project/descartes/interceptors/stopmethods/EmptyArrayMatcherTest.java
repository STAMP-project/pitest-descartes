package eu.stamp_project.descartes.interceptors.stopmethods;

import eu.stamp_project.test.input.Calculator;
import eu.stamp_project.test.input.StopMethods;


import java.lang.reflect.Method;
import java.util.stream.Stream;

import static eu.stamp_project.test.MethodStreamBuilder.fromClass;

public class EmptyArrayMatcherTest extends MethodMatcherTest {

    @Override
    Stream<Method> toIntercept () throws NoSuchMethodException {
        return fromClass(StopMethods.class)
                .method("emptyIntArray")
                .method("emptyInt2Array")
                .method("emptyMatrix")
                .method("emptyStringArray")
                .method("emptyStringMatrix")
                .toStream();
    }

    @Override
    Stream<Method> toAllow() throws NoSuchMethodException {
        return fromClass(Calculator.class)
                .method("getSomeCalculators", int.class)
                .method("getSomeMore", int.class)
                .method("getRange", int.class)
                .toStream();
    }

    @Override
    StopMethodMatcher getMatcher() {
        return StopMethodMatchers.returnsAnEmptyArray();
    }
}
