package eu.stamp_project.mutationtest.descartes.interceptors.stopmethods;

import eu.stamp_project.mutationtest.test.input.StopMethods;
import org.junit.Test;
import org.pitest.mutationtest.build.InterceptorParameters;
import org.pitest.mutationtest.build.MutationInterceptor;
import org.pitest.mutationtest.engine.MutationDetails;
import org.pitest.plugin.Feature;
import org.pitest.plugin.FeatureSetting;
import org.pitest.plugin.ToggleStatus;

import java.io.IOException;
import java.util.*;

import static eu.stamp_project.mutationtest.test.TestUtils.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;


public class StopMethodInterceptorTest {

    @Test
    public void shouldSkipAllMethods() throws IOException {
        assertThat(filteredMutations(StopMethods.class), is(empty()));
    }

    @Test
    public void shouldPutBackDeprecatedMethods() throws IOException {
        assertThat(filteredMutations(StopMethods.class, "deprecated"), hasSize(2));
    }

    @Test
    public void shouldSkipAllMethodsInKotlinDataClass() throws IOException {
        assertThat(filteredMutations(loadClass("tests.Person")), is(empty()));
    }

    private Collection<MutationDetails> filteredMutations(Class<?> target, String... exclusions) throws IOException {
        MutationInterceptor interceptor = getInterceptor(exclusions);
        interceptor.begin(getClassTree(target));
        return interceptor.intercept(findMutationPoints(target), null);
    }

    private MutationInterceptor getInterceptor(String... exclusions) throws IOException {
        StopMethodMatcherInterceptorFactory stopFactory = new StopMethodMatcherInterceptorFactory();
        Feature feature = stopFactory.provides();
        Map<String, List<String>> featureParameters = new HashMap<>();
        featureParameters.put("except", Arrays.asList(exclusions));
        FeatureSetting setting = new FeatureSetting(feature.name(), ToggleStatus.ACTIVATE, featureParameters);
        InterceptorParameters params = new InterceptorParameters(setting, null, null);
        return stopFactory.createInterceptor(params);
    }
}
