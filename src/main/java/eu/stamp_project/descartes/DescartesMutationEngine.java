package eu.stamp_project.descartes;

import eu.stamp_project.descartes.codemanipulation.MethodInfo;
import eu.stamp_project.descartes.operators.MutationOperator;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.pitest.classinfo.ClassByteArraySource;
import org.pitest.mutationtest.engine.Mutater;
import org.pitest.mutationtest.engine.MutationEngine;

public class DescartesMutationEngine implements MutationEngine {

  private final Predicate<MethodInfo> excludedMethods;

  private final Collection<MutationOperator> operators;

  public DescartesMutationEngine(final Collection<MutationOperator> operators) {
    this(m -> false, operators);
  }

  public DescartesMutationEngine(
      final Predicate<MethodInfo> excludedMethods, final Collection<MutationOperator> operators) {
    this.excludedMethods = Objects.requireNonNull(excludedMethods,
        "Excluded methods predicate can not be null");
    this.operators = Objects.requireNonNull(operators,
        "Collection of mutation operators can not be null");;
  }

  @Override
  public Mutater createMutator(final ClassByteArraySource byteSource) {
    return new DescartesMutater(byteSource, excludedMethods, operators);
  }

  @Override
  public Collection<String> getMutatorNames() {
    return operators.stream().map(MutationOperator::getIdentifier).collect(Collectors.toList());
  }

  @Override
  public String toString() {
    return "DescartesMutationEngine";
  }

  @Override
  public String getName() {
    return "descartes";
  }
}
