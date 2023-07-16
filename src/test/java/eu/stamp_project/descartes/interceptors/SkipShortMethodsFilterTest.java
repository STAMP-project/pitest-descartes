package eu.stamp_project.descartes.interceptors;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import eu.stamp_project.test.input.Calculator;
import java.io.IOException;
import java.lang.reflect.Method;
import org.junit.jupiter.api.Test;

class SkipShortMethodsFilterTest extends MutationFilterTest<SkipShortMethodsFilter> {

  @Override
  public SkipShortMethodsFilter getFilter() {
    return new SkipShortMethodsFilter(3);
  }

  @Test
  public void testAllowsLargeEnoughMethod() throws NoSuchMethodException, IOException {
    Method method = Calculator.class.getDeclaredMethod("getSomething");
    assertTrue(allows(method, "1.2f"));
  }

  @Test
  public void testInterceptShortMethod() throws IOException, NoSuchMethodException {
    Method method = Calculator.class.getDeclaredMethod("getCeiling");
    assertFalse(allows(method, "1"));
  }
}
