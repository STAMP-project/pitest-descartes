package eu.stamp_project.descartes.interceptors;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.lang.reflect.Method;
import org.junit.jupiter.api.Test;
import tests.Person;

public class AvoidNullInNotNullFilterTest extends MutationFilterTest<AvoidNullInNotNullFilter> {

  // NOTE: Not using parameterized tests to make explicit the intention of each test case.

  @Override
  public AvoidNullInNotNullFilter getFilter() {
    return new AvoidNullInNotNullFilter();
  }

  private Method method(String name, Class<?>... params) throws NoSuchMethodException {
    return tests.PersonKt.class.getDeclaredMethod(name, params);
  }

  @Test
  public void testNullOperatorNonNullResult() throws IOException, NoSuchMethodException {
    assertFalse(allows(method("getNonNullString"), "null"));
  }

  @Test
  public void testNullOperatorPotentialNullResult() throws NoSuchMethodException, IOException {
    assertTrue(allows(method("getPossiblyNullString", Person.class), "null"));
  }

  @Test
  public void testNonNullOperatorNonNullResult() throws NoSuchMethodException, IOException {
    assertTrue(allows(method("getNonNullString"), "\"A\""));
  }

  @Test
  public void testNonNullOperatorPotentiallyNulResult() throws NoSuchMethodException, IOException {
    assertTrue(allows(method("getPossiblyNullString", Person.class), "\"A\""));
  }
}
