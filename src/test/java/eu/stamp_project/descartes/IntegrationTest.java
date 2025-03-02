package eu.stamp_project.descartes;

import static eu.stamp_project.test.Utils.getClassTree;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

import eu.stamp_project.test.input.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
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
import tests.Person;

class IntegrationTest {

  @Test
  void testShouldNotFindMutationsInKotlinDataClass() throws IOException {
    assertThat(findMutationsWithDefaultOptions(Person.class), empty());
  }

  @Test
  void testShouldNotFinAnyStopMethod() throws IOException {
    assertThat(findMutationsWithDefaultOptions(StopMethods.class), empty());
  }

  @Test
  void shouldNotFindNativeMethods() throws IOException {
    assertThat(findMutations(NativeMethodClass.class, noFeatures()), empty());
  }

  @Test
  void shouldNotFindAnyMutationPointInInterface() throws IOException {
    assertThat(findMutationsWithDefaultOptions(Interface.class), empty());
  }

  @Test
  void shouldSkipMethodsWithPattern() throws IOException {
    Collection<MutationDetails> mutations = findMutations(Calculator.class, excludeMethods("get*"));
    Set<String> methods = methodsFrom(mutations);
    assertTrue(
        methods.stream().noneMatch(name -> name.startsWith("get")),
        "No mutated method should start by 'get'.");
  }

  @Test
  void shouldNotFindAbstractMethods() throws IOException {
    Collection<MutationDetails> mutations = findMutations(AbstractClass.class, noFeatures());
    Set<String> methods = methodsFrom(mutations);
    assertThat(methods, both(contains("nonAbstractMethod")).and(not(contains("abstractMethod"))));
  }

  private static Set<String> methodsFrom(Collection<MutationDetails> mutations) {
    return mutations.stream().map(MutationDetails::getMethod).collect(Collectors.toSet());
  }

  private static Collection<MutationDetails> findMutationsWithDefaultOptions(Class<?> target)
      throws IOException {
    return findMutations(target, defaultOptions());
  }

  private static Collection<MutationDetails> findMutations(Class<?> target, ReportOptions options)
      throws IOException {
    SettingsFactory factory = new SettingsFactory(options, PluginServices.makeForContextLoader());
    EngineArguments arguments =
        new EngineArguments(options.getMutators(), options.getExcludedMethods());
    MutationEngineFactory engineFactory = factory.createEngine();
    MutationEngine engine = engineFactory.createEngine(arguments);
    ClassByteArraySource source = ClassloaderByteArraySource.fromContext();
    CompoundInterceptorFactory interceptorFactory = factory.getInterceptor();
    // Interceptors with no coverage information and no test prioritization
    MutationInterceptor interceptor =
        interceptorFactory.createInterceptor(options, null, source, null, null);
    Mutater mutater = engine.createMutator(source);
    ClassName className = ClassName.fromClass(target);
    List<MutationDetails> mutations = mutater.findMutations(className);
    ClassTree classTree = getClassTree(target);
    interceptor.begin(classTree);
    return interceptor.intercept(mutations, mutater);
  }

  private static ReportOptions defaultOptions() {
    ReportOptions options = new ReportOptions();
    options.setMutationEngine("descartes");
    return options;
  }

  private static ReportOptions useFeatures(String... features) {
    ReportOptions options = defaultOptions();
    options.setFeatures(Arrays.asList(features));
    return options;
  }

  private static ReportOptions noFeatures() {
    return useFeatures("-DO_NOT_MUTATE()", "-AVOID_NULL", "-STOP_METHODS()");
  }

  private static ReportOptions excludeMethods(String... methods) {
    ReportOptions options = defaultOptions();
    options.setExcludedMethods(Arrays.asList(methods));
    return options;
  }
}
