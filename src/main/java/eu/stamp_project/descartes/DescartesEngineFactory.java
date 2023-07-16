package eu.stamp_project.descartes;

import eu.stamp_project.descartes.codemanipulation.MethodInfo;
import eu.stamp_project.descartes.operators.MutationOperator;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
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
      operators = DEFAULT_MUTATION_OPERATORS;
    }
    return new DescartesMutationEngine(
        globsToPredicate(engineArguments.excludedMethods()),
        MutationOperator.fromIdentifiers(operators)
    );
  }

  public static DescartesMutationEngine createDefaultEngine() {
    return new DescartesMutationEngine(getDefaultOperators());
  }

  public static final List<String> DEFAULT_MUTATION_OPERATORS =
      List.of(
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
          "\"A\""
      );

  public static Collection<MutationOperator> getDefaultOperators() {
    return MutationOperator.fromIdentifiers(DEFAULT_MUTATION_OPERATORS);
  }

  public static Predicate<MethodInfo> globsToPredicate(final Collection<String> globs) {
    Predicate<String> excludedNames = Prelude.or(Glob.toGlobPredicates(globs));
    return (method) -> excludedNames.test(method.getName());
  }

  @Override
  public String name() {
    return "descartes";
  }

  @Override
  public String description() {
    return "Extreme mutation engine";
  }
}
