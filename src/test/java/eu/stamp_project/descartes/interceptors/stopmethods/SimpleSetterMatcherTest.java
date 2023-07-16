package eu.stamp_project.descartes.interceptors.stopmethods;

import static eu.stamp_project.test.MethodStreamBuilder.fromClass;

import eu.stamp_project.test.input.Calculator;
import eu.stamp_project.test.input.StopMethods;
import java.lang.reflect.Method;
import java.util.stream.Stream;

class SimpleSetterMatcherTest extends MethodMatcherTest {

  @Override
  Stream<Method> toIntercept() throws NoSuchMethodException {
    return fromClass(StopMethods.class)
        .method("setABoolean", boolean.class)
        .method("setAByte", byte.class)
        .method("setAChar", char.class)
        .method("setADouble", double.class)
        .method("setAFloat", float.class)
        .method("setALong", long.class)
        .method("setAnInt", int.class)
        .method("setAShort", short.class)
        .method("setAString", String.class)
        .method("setAnObject", Object.class)
        .method("setStaticField", int.class)
        .method("setFluent", int.class)
        .toStream();
  }

  @Override
  Stream<Method> toAllow() throws NoSuchMethodException {
    return Stream.of(Calculator.class.getDeclaredMethod("setDoubleRegistry", double.class));
  }

  @Override
  MethodMatcher getMatcher() {
    return StopMethodMatchers.IS_SIMPLE_SETTER;
  }
}
