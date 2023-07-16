package eu.stamp_project.descartes.interceptors;

import org.pitest.mutationtest.build.InterceptorParameters;
import org.pitest.mutationtest.build.MutationInterceptor;
import org.pitest.mutationtest.build.MutationInterceptorFactory;
import org.pitest.plugin.Feature;

public class AvoidNullInNotNullFilterFactory implements MutationInterceptorFactory {

  private static final String DESCRIPTION =
      "Avoids null mutation in non-nullable methods, as marked by the Kotlin compiler";

  @Override
  public MutationInterceptor createInterceptor(InterceptorParameters interceptorParameters) {
    return new AvoidNullInNotNullFilter();
  }

  @Override
  public Feature provides() {
    return Feature.named("AVOID_NULL").withDescription(DESCRIPTION).withOnByDefault(true);
  }

  @Override
  public String description() {
    return DESCRIPTION;
  }
}
