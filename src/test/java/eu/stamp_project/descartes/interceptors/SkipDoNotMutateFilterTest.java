package eu.stamp_project.descartes.interceptors;

import static org.junit.jupiter.api.Assertions.*;

import eu.stamp_project.test.input.SkipFalseMutationOnAllButOne;
import eu.stamp_project.test.input.SkipMutationOnMethod;
import eu.stamp_project.test.input.SkipMutationTarget;
import java.io.IOException;
import java.lang.reflect.Method;
import org.junit.jupiter.api.Test;

class SkipDoNotMutateFilterTest extends MutationFilterTest {

  @Override
  public MutationFilter getFilter() {
    return new SkipDoNotMutateFilter();
  }

  @Test
  void testSkipMethodInAnnotatedClass() throws NoSuchMethodException, IOException {
    Method method = SkipMutationTarget.class.getDeclaredMethod("methodToSkip");
    assertFalse(allows(method, "void"));
  }

  @Test
  void testSkipAnnotatedMethodInNonAnnotatedClass()
      throws NoSuchMethodException, IOException {
    Method method = SkipMutationOnMethod.class.getDeclaredMethod("methodToSkip");
    assertFalse(allows(method, "void"));
  }

  @Test
  void testSkipSpecificMutationFromClassAnnotation()
      throws NoSuchMethodException, IOException {
    Method method = SkipFalseMutationOnAllButOne.class.getDeclaredMethod("returnFalse");
    assertFalse(allows(method, "false"));
  }

  @Test
  void testAllowSpecificMutationFromClassAnnotation()
      throws NoSuchMethodException, IOException {
    Method method = SkipFalseMutationOnAllButOne.class.getDeclaredMethod("returnFalse");
    assertTrue(allows(method, "true"));
  }

  @Test
  void testSkipSpecificMutationFromMethodAnnotation()
      throws NoSuchMethodException, IOException {
    Method method = SkipFalseMutationOnAllButOne.class.getDeclaredMethod("returnTrue");
    assertFalse(allows(method, "true"));
  }

  @Test
  void testAllowSpecificMutationAsPerMethodAnnotation()
      throws NoSuchMethodException, IOException {
    Method method = SkipFalseMutationOnAllButOne.class.getDeclaredMethod("returnTrue");
    assertTrue(allows(method, "false"));
  }
}
