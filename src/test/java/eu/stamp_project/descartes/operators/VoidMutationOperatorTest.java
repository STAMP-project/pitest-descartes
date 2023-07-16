package eu.stamp_project.descartes.operators;

import static eu.stamp_project.test.MethodStreamBuilder.fromClass;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import eu.stamp_project.test.input.Calculator;
import eu.stamp_project.test.input.SideEffect;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

class VoidMutationOperatorTest extends ParameterlessOperatorTest<VoidMutationOperator> {

  @Override
  VoidMutationOperator buildOperator() {
    return new VoidMutationOperator();
  }

  @Override
  Stream<Method> methodsToMutate() throws NoSuchMethodException {
    return fromClass(SideEffect.class).method("increment").toStream();
  }

  @Override
  Stream<Method> methodsToAvoid() throws NoSuchMethodException {
    return fromClass(Calculator.class).method("getCeiling").toStream();
  }

  @Test
  void testRemoveSideEffects()
      throws NoSuchMethodException, IOException, IllegalAccessException, InvocationTargetException,
          InstantiationException, NoSuchFieldException {
    // Save guard against errors in the input class\
    checkThereIsASideEffect();
    // Actual verification
    checkSideEffectWasRemoved();
  }

  private void checkThereIsASideEffect() {
    SideEffect instance = new SideEffect();
    int initialValue = instance.count;
    instance.increment();
    assertThat(
        "No detected side effect from method increment in class SideEffect. Value of field count remains the same after method invocation",
        instance.count,
        is(not(initialValue)));
  }

  private void checkSideEffectWasRemoved()
      throws NoSuchMethodException, IllegalAccessException, InvocationTargetException,
          InstantiationException, NoSuchFieldException, IOException {
    Class<?> mutant = mutate(SideEffect.class.getDeclaredMethod("increment"));
    Object instance = newInstance(mutant);
    int initialValue = mutant.getDeclaredField("count").getInt(instance);
    mutant.getDeclaredMethod("increment").invoke(instance);
    int result = mutant.getDeclaredField("count").getInt(instance);
    assertThat("Side effect was not removed after mutation.", result, is(initialValue));
  }
}
