package eu.stamp_project.descartes.interceptors.stopmethods;

import static eu.stamp_project.test.MethodStreamBuilder.fromClass;

import eu.stamp_project.test.input.Calculator;
import eu.stamp_project.test.input.StopMethods;
import java.lang.reflect.Method;
import java.util.stream.Stream;

class ReturnConstantMatcherTest extends MethodMatcherTest {

  @Override
  Stream<Method> toIntercept() throws NoSuchMethodException {
    return fromClass(StopMethods.class)
        .method("returnTrue")
        .method("returnByte")
        .method("returnShort")
        .method("returnNegativeInt")
        .method("returnPositiveInt")
        .method("returnOne")
        .method("returnPositiveLong")
        .method("returnNegativeLong")
        .method("returnPositiveFloat")
        .method("returnNegativeFloat")
        .method("returnPositiveDouble")
        .method("returnNegativeDouble")
        .method("returnChar")
        .method("returnString")
        .method("returnNull") // ?
        .toStream();
  }

  @Override
  Stream<Method> toAllow() throws NoSuchMethodException {
    return Stream.of(Calculator.class.getDeclaredMethod("conditionalReturn", boolean.class));
  }

  @Override
  MethodMatcher getMatcher() {
    return StopMethodMatchers.RETURNS_CONSTANT;
  }
}
