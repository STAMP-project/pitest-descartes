package eu.stamp_project.descartes.interceptors.stopmethods;

import static eu.stamp_project.test.MethodStreamBuilder.fromClass;

import eu.stamp_project.test.input.Calculator;
import eu.stamp_project.test.input.StopMethods;
import java.lang.reflect.Method;
import java.util.stream.Stream;

public class DelegateMethodMatcherTest extends MethodMatcherTest {

  @Override
  Stream<Method> toIntercept() throws NoSuchMethodException {
    return fromClass(StopMethods.class)
        .method("delegateSelfStaticVoid", int.class)
        .method("delegateSelfVoid", int.class)
        .method("delegateSelfValue", String.class, int.class, int.class)
        .method("delegateParameterValue", String.class, int.class, int.class)
        .method("delegateFieldValue", int.class, int.class)
        .method("delegateToExternalClass", String.class)
        .method("delegateToStatic", String.class)
        .method("delegateToStatic")
        .toStream();
  }

  @Override
  Stream<Method> toAllow() throws NoSuchMethodException {
    return fromClass(Calculator.class)
        .method("getMultipleCalculators", int.class)
        .method("isOdd")
        .method("getCeiling")
        .method("getSquare")
        .toStream();
  }

  @Override
  MethodMatcher getMatcher() {
    return StopMethodMatchers.IS_DELEGATE;
  }
}
