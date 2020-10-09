package eu.stamp_project.descartes.interceptors.stopmethods;

import eu.stamp_project.test.input.Calculator;
import eu.stamp_project.test.input.Numbers;

import java.lang.reflect.Method;
import java.util.stream.Stream;

import static eu.stamp_project.test.MethodStreamBuilder.fromClass;
import static eu.stamp_project.test.MethodStreamBuilder.select;

public class EnumMethodMatcherTest extends MethodMatcherTest{

    @Override
    Stream<Method> toIntercept() throws NoSuchMethodException {
        return fromClass(Numbers.class).allDeclaredMethods()
                .except("extraMethod")
                .toStream();
    }

    @Override
    Stream<Method> toAllow() throws NoSuchMethodException {
        return select(
                fromClass(Numbers.class).method("extraMethod"),
                fromClass(Calculator.class).method("getRange", int.class)
        );
    }

    @Override
    StopMethodMatcher getMatcher() {
        return StopMethodMatchers.isEnumGenerated();
    }
}
