package eu.stamp_project.descartes;

import eu.stamp_project.descartes.codemanipulation.MethodInfo;
import eu.stamp_project.descartes.operators.MutationOperator;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.pitest.functional.prelude.Prelude;
import org.pitest.mutationtest.EngineArguments;
import org.pitest.mutationtest.MutationEngineFactory;
import org.pitest.mutationtest.engine.MutationEngine;
import org.pitest.util.Glob;

public class DescartesEngineFactory implements MutationEngineFactory {

  @Override
  public MutationEngine createEngine(final EngineArguments engineArguments) {
    Collection<String> operators = engineArguments.mutators();
    if (operators == null || operators.isEmpty()) {
      operators = getDefaultOperators();
    }
    return createEngine(
        globsToPredicate(engineArguments.excludedMethods()),
        operators.stream().map(MutationOperator::fromIdentifier).collect(Collectors.toList()));
  }

  public MutationEngine createEngine(
      final Predicate<MethodInfo> excludedMethods, final Collection<MutationOperator> operators) {

    return new DescartesMutationEngine(excludedMethods, operators);
  }

  public static Collection<String> getDefaultOperators() {
    return Arrays.asList(
        "void",
        "null",
        "empty",
        "true",
        "false",
        "0",
        "1",
        "(short)0",
        "(short)1",
        "(byte)0",
        "(byte)1",
        "0L",
        "1L",
        "0.0",
        "1.0",
        "0.0f",
        "1.0f",
        "'\\40'",
        "'A'",
        "\"\"",
        "\"A\"");
  }

  public static Predicate<MethodInfo> globsToPredicate(final Collection<String> globs) {
    Predicate<String> excludedNames = Prelude.or(Glob.toGlobPredicates(globs));
    return (method) -> excludedNames.test(method.getName());
  }

  public String name() {
    return "descartes";
  }

  public String description() {
    return "Extreme mutation engine";
  }
}
