package eu.stamp_project.descartes.interceptors.stopmethods;

import static eu.stamp_project.test.MethodStreamBuilder.fromClass;

import eu.stamp_project.test.input.Calculator;
import eu.stamp_project.test.input.StopMethods;
import java.lang.reflect.Method;
import java.util.stream.Stream;

class ReturnsParameterMethodMatcherTest extends MethodMatcherTest {

  @Override
  Stream<Method> toIntercept() throws NoSuchMethodException {
    return fromClass(StopMethods.class)
        .method("onlyReturnsAPrimitiveParameter", int.class, int.class, int.class)
        .method("onlyReturnsAReferenceParameter", int.class, int.class, String.class)
        .toStream();
  }

  @Override
  Stream<Method> toAllow() throws NoSuchMethodException {
    return fromClass(Calculator.class)
        .method("getSomeCalculators", int.class)
        .method("add", double.class)
        .toStream();
  }

  @Override
  MethodMatcher getMatcher() {
    return StopMethodMatchers.RETURNS_PARAMETER;
  }
}
