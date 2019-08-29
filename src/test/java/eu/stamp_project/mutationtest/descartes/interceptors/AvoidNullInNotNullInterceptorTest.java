package eu.stamp_project.mutationtest.descartes.interceptors;

import eu.stamp_project.mutationtest.descartes.DescartesMutationEngine;
import eu.stamp_project.mutationtest.descartes.MutationPointFinder;
import eu.stamp_project.mutationtest.descartes.operators.NullMutationOperator;
import org.junit.Test;
import org.pitest.bytecode.analysis.ClassTree;
import org.pitest.classinfo.ClassName;
import org.pitest.mutationtest.build.InterceptorParameters;
import org.pitest.mutationtest.build.MutationInterceptor;
import org.pitest.mutationtest.engine.MutationDetails;
import org.pitest.plugin.Feature;
import org.pitest.plugin.FeatureSetting;
import org.pitest.plugin.ToggleStatus;


import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static eu.stamp_project.mutationtest.test.TestUtils.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;

public class AvoidNullInNotNullInterceptorTest {


    private Collection<MutationDetails> getNullMutationPointsFor(String className, String path) throws IOException {
        MutationPointFinder finder = new MutationPointFinder(
                ClassName.fromString(className),
                new DescartesMutationEngine(new NullMutationOperator())
        );
        org.pitest.reloc.asm.ClassReader reader = new  org.pitest.reloc.asm.ClassReader(getClass().getResourceAsStream(path));
        reader.accept(finder, 0);
        return finder.getMutationPoints();
    }

    private MutationInterceptor getInterceptor() {
        AvoidNullInNotNullInterceptorFactory factory = new AvoidNullInNotNullInterceptorFactory();
        Feature feature = factory.provides();
        FeatureSetting setting = new FeatureSetting(feature.name(), ToggleStatus.ACTIVATE, Collections.emptyMap());
        InterceptorParameters params = new InterceptorParameters(setting, null, null);
        return new AvoidNullInNotNullInterceptorFactory().createInterceptor(params);
    }


    @Test
    public void shouldFindOnlyNullableMethods() throws IOException {
        Class<?> target = loadClass("tests.PersonKt");
        Collection<MutationDetails> mutationPoints = findMutationPoints(target, "null");
        MutationInterceptor interceptor = getInterceptor();
        interceptor.begin(getClassTree(target));
        Collection<MutationDetails> filteredMutations = interceptor.intercept(mutationPoints, null);
        Collection<String> methods = filteredMutations.stream().map(details -> details.getMethod().name()).collect(Collectors.toList());
        assertThat(methods, contains("getPossiblyNullString"));
    }

}