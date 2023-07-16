package eu.stamp_project.descartes.interceptors.stopmethods;

import static eu.stamp_project.test.MethodStreamBuilder.fromClass;

import eu.stamp_project.test.input.Calculator;
import eu.stamp_project.test.input.StopMethods;
import java.lang.reflect.Method;
import java.util.stream.Stream;

class ToStringMethodMatcherTest extends MethodMatcherTest {

  @Override
  Stream<Method> toIntercept() throws NoSuchMethodException {
    return Stream.of(StopMethods.class.getDeclaredMethod("toString"));
  }

  @Override
  Stream<Method> toAllow() throws NoSuchMethodException {
    return fromClass(Calculator.class)
        .method("getScreen") // Same signature
        .method("toString", String.class) // Same name
        .toStream();
  }

  @Override
  MethodMatcher getMatcher() {
    return StopMethodMatchers.IS_TO_STRING;
  }
}
