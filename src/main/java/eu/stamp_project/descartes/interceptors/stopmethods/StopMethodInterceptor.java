package eu.stamp_project.descartes.interceptors.stopmethods;

import eu.stamp_project.descartes.interceptors.MutationFilter;
import java.util.Optional;
import org.pitest.bytecode.analysis.MethodTree;
import org.pitest.mutationtest.engine.MutationDetails;

public class StopMethodInterceptor extends MutationFilter {

  private final MethodMatcher matcher;

  public StopMethodInterceptor(MethodMatcher matcher) {
    this.matcher = matcher;
  }

  @Override
  public boolean allows(MutationDetails details) {
    Optional<MethodTree> methodTree = getMethod(details);
    return methodTree.isPresent() && !matcher.matches(getCurrentClass(), methodTree.get());
  }
}
