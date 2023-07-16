package eu.stamp_project.descartes.operators;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import eu.stamp_project.descartes.codemanipulation.MethodInfo;
import eu.stamp_project.test.input.SideEffect;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.junit.jupiter.api.Test;

class VoidWrapperTest extends MutationOperatorTest {

  /*
  This test class is pretty much similar to VoidMutationOperatorTest.
  However, the Void wrapper type is handled by the NullMutationOperator.
  This is a strange case.
  Conceptually, the Void type might be better handled by the VoidMutationOperator.
  However, doing this, will create code duplication and extra verifications
  in the code of both operators, Null.. and Void...
  Therefore, to keep it simple, we leave it like that for the moment.
  This test class exists just to highlight this fact and in case we decide
  to move the code to VoidMutationOperator.
   */
  
  

  @Test
  void testCanMutateVoidWrapper() throws NoSuchMethodException {
    MethodInfo methodInfo = new MethodInfo(getMethodReturningVoidWrapper());
    NullMutationOperator nullMutationOperator = new NullMutationOperator();
    assertTrue(nullMutationOperator.canMutate(methodInfo), "The null mutation operator should be able to mutate a method whose return type is Void");
  }

  private static Method getMethodReturningVoidWrapper() throws NoSuchMethodException {
    return SideEffect.class.getDeclaredMethod("nextOdd");
  }

  @Test
  void testSideEffectsRemovedInMutation()
      throws NoSuchMethodException, IOException, InstantiationException, IllegalAccessException,
          InvocationTargetException, NoSuchFieldException {
    checkOriginalSideEffect();
    checkSideEffectsRemoved();
  }

  private void checkOriginalSideEffect() {
    SideEffect obj = new SideEffect();
    int initialValue = obj.count;
    obj.nextOdd();
    assertNotEquals(
        initialValue,
        obj.count,
        "Method nextOdd in SideEffect test class has no effect on the count field");
  }

  private void checkSideEffectsRemoved()
      throws NoSuchMethodException, IOException, IllegalAccessException, InstantiationException,
          InvocationTargetException, NoSuchFieldException {
    Method method = getMethodReturningVoidWrapper();
    Class<?> mutant = mutate(method, new NullMutationOperator());
    Object instance = newInstance(mutant);
    Object initialValue = mutant.getDeclaredField("count").get(instance);
    Method mutatedMethod = mutant.getDeclaredMethod(method.getName(), method.getParameterTypes());
    mutatedMethod.invoke(instance);
    Object result = mutant.getDeclaredField("count").get(instance);
    assertThat("Field count should have changed after mutation", result, is(initialValue));
  }
}
