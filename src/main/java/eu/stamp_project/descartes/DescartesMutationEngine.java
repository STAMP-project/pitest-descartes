package eu.stamp_project.descartes;

import eu.stamp_project.descartes.codemanipulation.MethodInfo;
import eu.stamp_project.descartes.operators.MutationOperator;
import org.pitest.classinfo.ClassByteArraySource;
import org.pitest.mutationtest.engine.Mutater;
import org.pitest.mutationtest.engine.MutationEngine;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;


public class DescartesMutationEngine implements  MutationEngine {

    private final Predicate<MethodInfo> excludedMethods;

    private final Collection<MutationOperator> operators;

    public DescartesMutationEngine(Collection<MutationOperator> operators) {
        this(m -> false, operators);
    }

    @SuppressWarnings("unchecked")
    public DescartesMutationEngine(MutationOperator... operators) {
        this(Arrays.<MutationOperator>asList(operators));
    }

    @SuppressWarnings("unchecked")
    public DescartesMutationEngine(Predicate<MethodInfo> excludedMethods, MutationOperator...operators) {
        this(excludedMethods, Arrays.<MutationOperator>asList(operators));
    }

    public DescartesMutationEngine(Predicate<MethodInfo> excludedMethods, Collection<MutationOperator> operators) {
        Objects.requireNonNull(excludedMethods, "Excluded methods predicate can not be null");
        this.excludedMethods = excludedMethods;

        Objects.requireNonNull(operators, "Collection of mutation operators can not be null");
        this.operators = operators;
    }

    public Mutater createMutator(final ClassByteArraySource byteSource) {

        return new DescartesMutater(byteSource, excludedMethods, operators);
    }

    public Collection<String> getMutatorNames() {
       return operators.stream().map(MutationOperator::getIdentifier).collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "DescartesMutationEngine"; //TODO: Add more details here
    }

    @Override
    public String getName() { return "descartes"; }
}
