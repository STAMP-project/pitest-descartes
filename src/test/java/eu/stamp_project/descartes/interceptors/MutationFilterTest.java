package eu.stamp_project.descartes.interceptors;

import static eu.stamp_project.test.Utils.getClassTree;
import static eu.stamp_project.test.Utils.toMutationIdentifier;

import java.io.IOException;
import java.lang.reflect.Method;
import org.pitest.mutationtest.engine.MutationDetails;

public abstract class MutationFilterTest<T extends MutationFilter> {

  public abstract T getFilter();

  protected MutationDetails getMutationDetails(Method method, String operator) {
    return new MutationDetails(toMutationIdentifier(method, operator), "", "", 0, 0);
  }

  protected boolean allows(Method method, String mutator) throws IOException {
    MutationFilter filter = getFilter();
    filter.begin(getClassTree(method.getDeclaringClass()));
    return filter.allows(getMutationDetails(method, mutator));
  }
}
