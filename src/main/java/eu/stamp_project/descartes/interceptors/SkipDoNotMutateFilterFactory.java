package eu.stamp_project.descartes.interceptors;

import org.pitest.mutationtest.build.InterceptorParameters;
import org.pitest.mutationtest.build.MutationInterceptor;
import org.pitest.mutationtest.build.MutationInterceptorFactory;
import org.pitest.plugin.Feature;

public class SkipDoNotMutateFilterFactory implements MutationInterceptorFactory {

  private static final String DESCRIPTION =
      "Skips any method having an annotation whose simple name is DoNotMutate "
          + "or any method declared in a class with such annotation.";

  @Override
  public MutationInterceptor createInterceptor(InterceptorParameters interceptorParameters) {
    return new SkipDoNotMutateFilter();
  }

  @Override
  public Feature provides() {
    return Feature.named("DO_NOT_MUTATE").withDescription(DESCRIPTION).withOnByDefault(true);
  }

  @Override
  public String description() {
    return DESCRIPTION;
  }
}
