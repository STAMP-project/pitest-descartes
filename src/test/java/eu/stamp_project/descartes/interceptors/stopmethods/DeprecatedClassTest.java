package eu.stamp_project.descartes.interceptors.stopmethods;

import static eu.stamp_project.test.MethodStreamBuilder.fromClass;
import static eu.stamp_project.test.MethodStreamBuilder.select;

import eu.stamp_project.test.input.Calculator;
import eu.stamp_project.test.input.DeprecatedClass;
import eu.stamp_project.test.input.StopMethods;
import java.lang.reflect.Method;
import java.util.stream.Stream;

public class DeprecatedClassTest extends MethodMatcherTest {

  @Override
  Stream<Method> toIntercept() throws NoSuchMethodException {
    return select(
        fromClass(DeprecatedClass.class)
            .method("methodInsideDeprecatedClass")
            .method("deprecatedInDeprecatedClass"),
        fromClass(StopMethods.class).method("isDeprecated"));
  }

  @Override
  Stream<Method> toAllow() throws NoSuchMethodException {
    return Stream.of(Calculator.class.getDeclaredMethod("getCeiling"));
  }

  @Override
  MethodMatcher getMatcher() {
    return StopMethodMatchers.IS_DEPRECATED;
  }
}
