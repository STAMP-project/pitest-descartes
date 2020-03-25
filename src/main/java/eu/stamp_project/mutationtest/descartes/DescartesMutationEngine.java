package eu.stamp_project.mutationtest.descartes;

import eu.stamp_project.mutationtest.descartes.operators.MutationOperator;
import eu.stamp_project.mutationtest.descartes.operators.ThisMutationOperator;
import org.pitest.classinfo.ClassByteArraySource;
import org.pitest.classinfo.ClassName;
import org.pitest.mutationtest.engine.Mutater;
import org.pitest.mutationtest.engine.MutationEngine;
import org.pitest.reloc.asm.commons.Method;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.function.Predicate;


public class DescartesMutationEngine implements  MutationEngine {

    private final Predicate<Method> excludedMethods;

    private final Collection<MutationOperator> operators;


    public DescartesMutationEngine(Collection<MutationOperator> operators) {

        this(m -> false, operators);
    }

    @SuppressWarnings("unchecked")
    public DescartesMutationEngine(MutationOperator... operators) {
        this(Arrays.<MutationOperator>asList(operators));
    }

    @SuppressWarnings("unchecked")
    public DescartesMutationEngine(Predicate<Method> excludedMethods, MutationOperator...operators) {
        this(excludedMethods, Arrays.<MutationOperator>asList(operators));
    }

    public DescartesMutationEngine(Predicate<Method> excludedMethods, Collection<MutationOperator> operators) {
        if(excludedMethods == null) throw new IllegalArgumentException("excludedMethod argument can not be null");
        if(operators == null) throw new IllegalArgumentException("operators argument can not be null");

        this.excludedMethods = excludedMethods;
        this.operators = operators;

    }

    public Mutater createMutator(final ClassByteArraySource byteSource) {

        return new DescartesMutater(byteSource, this);
    }

    public Collection<String> getMutatorNames() {
       return operators.stream().map(op -> op.getID()).collect(Collectors.toList());
    }

    public Collection<MutationOperator> getOperatorsFor(final ClassName className, final Method method) {
        ThisMutationOperator thisOperator = new ThisMutationOperator();
        if(excludedMethods.test(method))
            return Collections.<MutationOperator>emptyList();
        Collection<MutationOperator> mutationOperators = operators.stream().filter(op -> op.canMutate(className, method)).collect(Collectors.toList());
        if(thisOperator.canMutate(className, method)) {
            mutationOperators.add(thisOperator);
        }
        return mutationOperators;
    }

    @Override
    public String toString() {
        return "DescartesMutationEngine"; //TODO: Add more details here
    }

    @Override
    public String getName() { return "descartes"; }
}
