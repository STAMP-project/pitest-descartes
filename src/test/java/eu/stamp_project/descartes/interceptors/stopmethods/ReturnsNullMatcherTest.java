package eu.stamp_project.descartes.interceptors.stopmethods;

import eu.stamp_project.test.input.Calculator;
import eu.stamp_project.test.input.StopMethods;
import org.pitest.bytecode.analysis.MethodTree;

import java.lang.reflect.Method;
import java.util.stream.Stream;

import static eu.stamp_project.test.MethodStreamBuilder.fromClass;

class ReturnsNullMatcherTest extends MethodMatcherTest {

    @Override
    public Stream<Method> toIntercept() throws NoSuchMethodException {
        return Stream.of(StopMethods.class.getDeclaredMethod("returnNull"));
    }

    @Override
    Stream<Method> toAllow() throws NoSuchMethodException {
        return fromClass(Calculator.class)
                .method("getClone")
                .method("getConditionalClone", boolean.class)
                .toStream();
    }

    @Override
    public StopMethodMatcher getMatcher() {
        return StopMethodMatchers.returnsNull();
    }
}
