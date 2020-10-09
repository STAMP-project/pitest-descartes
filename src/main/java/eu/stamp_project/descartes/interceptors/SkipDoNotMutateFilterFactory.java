package eu.stamp_project.descartes.interceptors;

import org.pitest.mutationtest.build.InterceptorParameters;
import org.pitest.mutationtest.build.MutationInterceptor;
import org.pitest.mutationtest.build.MutationInterceptorFactory;
import org.pitest.plugin.Feature;

public class SkipDoNotMutateFilterFactory implements MutationInterceptorFactory {

    private static final String DESCRIPTION = "Skips any method annotated with an annotation whose simple name is DoNotMutate or declared in a class likewise annotated.";

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
