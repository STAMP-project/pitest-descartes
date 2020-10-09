package eu.stamp_project.descartes;

import eu.stamp_project.descartes.interceptors.AvoidNullInNotNullFilterFactory;
import eu.stamp_project.descartes.interceptors.SkipDoNotMutateFilterFactory;
import eu.stamp_project.descartes.interceptors.stopmethods.StopMethodMatcherInterceptorFactory;
import eu.stamp_project.descartes.reporting.IssuesReportFactory;
import eu.stamp_project.descartes.reporting.JSONReportFactory;
import eu.stamp_project.descartes.reporting.MethodTestingFactory;
import eu.stamp_project.test.input.*;
import org.junit.jupiter.api.Test;
import org.pitest.bytecode.analysis.ClassTree;
import org.pitest.classinfo.ClassByteArraySource;
import org.pitest.classinfo.ClassName;
import org.pitest.classpath.ClassloaderByteArraySource;
import org.pitest.mutationtest.EngineArguments;
import org.pitest.mutationtest.MutationEngineFactory;
import org.pitest.mutationtest.build.CompoundInterceptorFactory;
import org.pitest.mutationtest.build.MutationInterceptor;
import org.pitest.mutationtest.config.PluginServices;
import org.pitest.mutationtest.config.ReportOptions;
import org.pitest.mutationtest.config.SettingsFactory;
import org.pitest.mutationtest.engine.Mutater;
import org.pitest.mutationtest.engine.MutationDetails;
import org.pitest.mutationtest.engine.MutationEngine;
import org.pitest.plugin.ClientClasspathPlugin;
import org.pitest.plugin.ToolClasspathPlugin;
import tests.Person;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static eu.stamp_project.test.Utils.getClassTree;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IntegrationTest {

    @Test
    void testShouldNotFindMutationsInKotlinDataClass() throws NoSuchMethodException, InstantiationException, IllegalAccessException, IOException, InvocationTargetException {
        Collection<MutationDetails> mutations = findMutationsWithDefaultOptions(Person.class);
        assertThat(mutations, empty());
    }

    @Test
    void testShouldNotFinAnyStopMethod() throws NoSuchMethodException, InstantiationException, IllegalAccessException, IOException, InvocationTargetException {
        Collection<MutationDetails> mutations = findMutationsWithDefaultOptions(StopMethods.class);
        assertThat(mutations, empty());
    }

    @Test
    void shouldSkipMethodsWithPattern() throws IOException {
        Collection<MutationDetails> mutations = findMutations(Calculator.class, excludeMethods("get*"));
        Set<String> methods = methodsFrom(mutations);
        assertTrue(methods.stream().allMatch( name -> !name.startsWith("get")), "No mutated method should start by 'get'.");
    }

    @Test
    public void shouldNotFindNativeMethods() throws IOException  {
        assertThat(findMutations(NativeMethodClass.class, noFeatures()), empty());
    }

    @Test
    public void shouldNotFindAbstractMethods() throws IOException {
        Set<String> methods = methodsFrom(findMutations(AbstractClass.class, noFeatures()));
        assertThat(methods, both(contains("nonAbstractMethod")).and(not(contains("abstractMethod"))));
    }

    @Test
    public void shouldNotFindAnyMutationPointInInterface() throws IOException {
        assertThat(findMutationsWithDefaultOptions(Interface.class), empty());
    }

    Set<String> methodsFrom(Collection<MutationDetails> mutations) {
        return mutations.stream().map(details -> details.getMethod().toString()).collect(Collectors.toSet());
    }

    Collection<MutationDetails> findMutationsWithDefaultOptions(Class<?> target) throws IOException {
        return findMutations(target, defaultOptions());
    }

    Collection<MutationDetails> findMutations(Class<?> target, ReportOptions options) throws IOException {
        SettingsFactory factory = new SettingsFactory(options, stubPluginServices());
        EngineArguments arguments = new EngineArguments(options.getMutators(), options.getExcludedMethods());
        MutationEngineFactory engineFactory = factory.createEngine();
        MutationEngine engine = engineFactory.createEngine(arguments);
        ClassByteArraySource source = ClassloaderByteArraySource.fromContext();
        CompoundInterceptorFactory interceptorFactory = factory.getInterceptor();
        MutationInterceptor interceptor = interceptorFactory.createInterceptor(options, source);
        Mutater mutater = engine.createMutator(source);
        ClassName className = ClassName.fromClass(target);
        List<MutationDetails> mutations = mutater.findMutations(className);
        ClassTree classTree = getClassTree(target);
        interceptor.begin(classTree);
        return interceptor.intercept(mutations, mutater);
    }

    PluginServices stubPluginServices() {
        return new PluginServices(getClass().getClassLoader()) {
            @Override
            public Iterable<? extends ClientClasspathPlugin> findClientClasspathPlugins() {
                return List.of(new DescartesEngineFactory());
            }

            @Override
            public Iterable<? extends ToolClasspathPlugin> findToolClasspathPlugins() {
                return List.of(
                        //Interceptors
                        new AvoidNullInNotNullFilterFactory(),
                        new SkipDoNotMutateFilterFactory(),
                        new StopMethodMatcherInterceptorFactory(),
                        //Reporters
                        new IssuesReportFactory(),
                        new JSONReportFactory(),
                        new MethodTestingFactory()
                );
            }
        };
    }

    ReportOptions defaultOptions() {
        ReportOptions options = new ReportOptions();
        options.setMutationEngine("descartes");
        return options;
    }

    ReportOptions useFeatures(String... features) {
        ReportOptions options = defaultOptions();
        options.setFeatures(Arrays.asList(features));
        return options;
    }

    ReportOptions noFeatures() {
        ReportOptions options = defaultOptions();
        options.setFeatures(List.of("-DO_NOT_MUTATE()", "-AVOID_NULL", "-STOP_METHODS()"));
        return options;
    }

    ReportOptions excludeMethods(String... methods) {
        ReportOptions options = defaultOptions();
        options.setExcludedMethods(Arrays.asList(methods));
        return options;
    }

    ReportOptions options(String[] features, String[] methods) {
        ReportOptions options = useFeatures(features);
        options.setExcludedMethods(Arrays.asList(methods));
        return options;
    }

}
