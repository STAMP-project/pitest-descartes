package eu.stamp_project.mutationtest.descartes.stopmethods;

import eu.stamp_project.mutationtest.descartes.DescartesEngineFactory;
import eu.stamp_project.mutationtest.descartes.DescartesMutationEngine;
import eu.stamp_project.mutationtest.descartes.MutationPointFinder;
import eu.stamp_project.mutationtest.test.StopMethods;
import org.junit.Before;
import org.junit.Test;
import org.pitest.classinfo.ClassName;
import org.pitest.mutationtest.EngineArguments;
import org.pitest.mutationtest.build.InterceptorParameters;
import org.pitest.mutationtest.build.MutationInterceptor;
import org.pitest.mutationtest.engine.MutationDetails;
import org.pitest.plugin.Feature;
import org.pitest.plugin.FeatureSetting;
import org.pitest.plugin.ToggleStatus;
import org.pitest.reloc.asm.ClassReader;

import java.io.IOException;
import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class StopMethodInterceptorTest {

    public Collection<MutationDetails> mutationPoints;


    @Before
    public void findMutationPoints() throws IOException  {
        DescartesMutationEngine engine = (DescartesMutationEngine) new DescartesEngineFactory().createEngine(EngineArguments.arguments());
        MutationPointFinder finder = new MutationPointFinder(ClassName.fromClass(StopMethods.class), engine);
        ClassReader reader = new ClassReader(StopMethods.class.getName());
        reader.accept(finder, 0);

        mutationPoints = finder.getMutationPoints();
    }

    public MutationInterceptor getInterceptor(String... exclusions) {
        StopMethodMatcherInterceptorFactory stopFactory = new StopMethodMatcherInterceptorFactory();
        Feature feature = stopFactory.provides();
        Map<String, List<String>> featureParameters = new HashMap<>();
        featureParameters.put("except", Arrays.asList(exclusions));
        FeatureSetting setting = new FeatureSetting(feature.name(), ToggleStatus.ACTIVATE, featureParameters);
        InterceptorParameters params = new InterceptorParameters(setting, null, null);
        return stopFactory.createInterceptor(params);
    }

    @Test
    public void shouldSkipAllMethods() throws IOException {
        MutationInterceptor interceptor = getInterceptor();
        interceptor.begin(BaseMethodMatcherTest.getClassTree(StopMethods.class));
        assertThat(interceptor.intercept(mutationPoints, null), is(emptyIterable()));
    }

    @Test
    public void shouldPutBackDeprecatedMethods() throws IOException {
        MutationInterceptor interceptor = getInterceptor("deprecated");
        interceptor.begin(BaseMethodMatcherTest.getClassTree(StopMethods.class));
        //isDeprecated method in StopMethods is boolean so we expect two potential mutations
        assertThat(interceptor.intercept(mutationPoints, null), hasSize(2));
    }


}
