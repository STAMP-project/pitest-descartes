package eu.stamp_project.descartes.interceptors;

import java.util.Optional;
import org.pitest.mutationtest.build.InterceptorParameters;
import org.pitest.mutationtest.build.MutationInterceptor;
import org.pitest.mutationtest.build.MutationInterceptorFactory;
import org.pitest.plugin.Feature;
import org.pitest.plugin.FeatureParameter;

public class SkipShortMethodsFilterFactory implements MutationInterceptorFactory {

  protected static final int DEFAULT_THRESHOLD = 5;
  private static final String DESCRIPTION =
      "Skips any method shorter than a given number of lines. Default threshold is "
          + DEFAULT_THRESHOLD;
  private static final FeatureParameter LINES =
      FeatureParameter.named("lines")
          .withDescription("Threshold for the size of the methods in lines.");

  @Override
  public MutationInterceptor createInterceptor(InterceptorParameters interceptorParameters) {
    Optional<Integer> lines = interceptorParameters.getInteger(LINES);
    return new SkipShortMethodsFilter(lines.orElse(DEFAULT_THRESHOLD));
  }

  @Override
  public Feature provides() {
    return Feature.named("SKIP_SHORT")
        .withDescription(DESCRIPTION)
        .withOnByDefault(false)
        .withParameter(LINES);
  }

  @Override
  public String description() {
    return DESCRIPTION;
  }
}
