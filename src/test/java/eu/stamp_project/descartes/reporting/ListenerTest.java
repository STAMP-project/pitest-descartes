package eu.stamp_project.descartes.reporting;

import static eu.stamp_project.test.MethodMutationResultsBuilder.classMutationResults;
import static eu.stamp_project.test.MethodMutationResultsBuilder.method;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Named.named;

import eu.stamp_project.descartes.DescartesEngineFactory;
import eu.stamp_project.test.InMemoryReportStrategy;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.stream.Stream;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.pitest.mutationtest.*;
import org.pitest.mutationtest.config.PluginServices;
import org.pitest.mutationtest.config.ReportOptions;
import org.pitest.util.ResultOutputStrategy;

public abstract class ListenerTest<TFactory extends MutationResultListenerFactory> {

  Class<TFactory> factoryClass;

  ListenerTest(Class<TFactory> factoryClass) {
    this.factoryClass = factoryClass;
  }

  @Test
  void testFactoryCanBeDiscovered() {
    assertTrue(
        PluginServices
            .makeForContextLoader()
            .findToolClasspathPlugins()
            .stream()
            .anyMatch(
                plugin -> factoryClass.isInstance(plugin)
            )
    );
  }

  TFactory getFactory()
      throws NoSuchMethodException, InvocationTargetException, InstantiationException,
      IllegalAccessException {
    return factoryClass.getConstructor().newInstance();
  }

  MutationResultListener getListener(ListenerArguments arguments)
      throws InvocationTargetException, NoSuchMethodException, InstantiationException,
      IllegalAccessException {
    return getFactory().getListener(new Properties(), arguments);
  }

  ListenerArguments getArgumentsWithOutputStrategy(ResultOutputStrategy outputStrategy) {
    return new ListenerArguments(
        outputStrategy,
        null, // No coverage information
        null, // No source location
        DescartesEngineFactory.createDefaultEngine(),
        1704638222, //Arbitrary start time
        false, // no full mutation matrix
        new ReportOptions(),
        new ArrayList<>()
    );
  }

  MutationResultListener getListener(ResultOutputStrategy outputStrategy)
      throws InvocationTargetException, NoSuchMethodException, InstantiationException,
      IllegalAccessException {
    return getListener(getArgumentsWithOutputStrategy(outputStrategy));
  }

  MutationResultListener getListener()
      throws InvocationTargetException, NoSuchMethodException, InstantiationException,
      IllegalAccessException {
    return getListener(getArgumentsWithOutputStrategy(new InMemoryReportStrategy()));
  }
  protected static void handleMutationResults(MutationResultListener listener, Collection<ClassMutationResults> results) {
    listener.runStart();
    for (ClassMutationResults classResults : results) {
      listener.handleMutationResult(classResults);
    }
    listener.runEnd();
  }

  protected static Stream<Named<Collection<ClassMutationResults>>> provideClassMutationResults() {
    return Stream.of(
        named("Single class mutation results with tested method",
            List.of(
                classMutationResults(
                    method("()V").coveredInOrderBy("test1", "test2")
                        .withDetectedMutation("void", "test1")
                )
            )
        ),
        named("Single class mutation results with pseudo-tested method",
            List.of(
                classMutationResults(
                    method("(I)I").coveredInOrderBy("test1")
                        .withNonKilledMutation("0")
                        .withNonKilledMutation("1")
                )
            )
        ),
        named("Single class mutation results with partially tested method",
            List.of(
                classMutationResults(
                    method("()Z").coveredInOrderBy("test1", "test2")
                        .withDetectedMutation("true", "test2")
                        .withNonKilledMutation("false")
                )
            )
        ),
        named("Multiple classes mutation results",
            List.of(
                classMutationResults(
                    "example.Class1",
                    method("m1", "()Lexample/Result;")
                        .withNonCoveredMutation("null")
                ),
                classMutationResults(
                    "example/Class2",
                    method("n1", "(Lexample/Class3;)V").coveredInOrderBy("test1")
                        .withDetectedMutation("void", "test1"),
                    method("n2", "()I").coveredInOrderBy("test1", "test2")
                        .withDetectedMutation("1", "test2")
                        .withNonKilledMutation("0")
                )
            )
        ),
        named("Mutation results with error",
            List.of(
                classMutationResults(
                    method("()V").coveredInOrderBy("test1")
                        .withMutation("void", DetectionStatus.RUN_ERROR, null)
                )
            )
        )
    );
  }

}
