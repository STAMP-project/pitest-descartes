package eu.stamp_project.descartes.interceptors.stopmethods;

import static eu.stamp_project.test.MethodStreamBuilder.fromClass;

import eu.stamp_project.test.input.Calculator;
import eu.stamp_project.test.input.StopMethods;
import java.lang.reflect.Method;
import java.util.stream.Stream;

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
  public MethodMatcher getMatcher() {
    return StopMethodMatchers.RETURNS_NULL;
  }
}
