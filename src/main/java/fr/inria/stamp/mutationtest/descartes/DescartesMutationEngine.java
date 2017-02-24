package fr.inria.stamp.mutationtest.descartes;

import java.util.Set;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import edu.emory.mathcs.backport.java.util.Arrays;
import org.pitest.classinfo.ClassByteArraySource;
import org.pitest.functional.F;
import org.pitest.functional.predicate.False;
import org.pitest.functional.predicate.Predicate;
import org.pitest.functional.FCollection;
import org.pitest.mutationtest.engine.MutationEngine;
import org.pitest.mutationtest.engine.Mutater;

import org.pitest.reloc.asm.commons.Method;

import fr.inria.stamp.mutationtest.descartes.operators.MutationOperator;

public class DescartesMutationEngine implements  MutationEngine {

    private final Predicate<Method> excludedMethods;

    private final Set<String> logginClasses;

    //TODO: Handle operator creation and selection
    private final Collection<MutationOperator> operators;

    public DescartesMutationEngine(Collection<MutationOperator> operators) {
        this(False.<Method>instance(), Collections.<String>emptySet(), operators);
    }

    public DescartesMutationEngine(MutationOperator... operators) {
        this(Arrays.asList(operators));
    }

    public DescartesMutationEngine(Predicate<Method> excludedMethods, Set<String> logginClasses, MutationOperator...operators) {
        this(excludedMethods, logginClasses, Arrays.asList(operators));
    }

    public DescartesMutationEngine(Predicate<Method> excludedMethods, Set<String> logginClasses, Collection<MutationOperator> operators) {
        if(excludedMethods == null) throw new IllegalArgumentException("excludedMethod argument can not be null");
        if(logginClasses == null) throw  new IllegalArgumentException("logginClasses argument can not be null");
        if(operators == null) throw new IllegalArgumentException("operators argument can not be null");

        this.excludedMethods = excludedMethods;
        this.logginClasses = logginClasses;
        this.operators = operators;

    }

    public Mutater createMutator(final ClassByteArraySource byteSource) {
        return new DescartesMutater(byteSource, this);
    }

    public Collection<String> getMutatorNames() {
        return FCollection.map(operators, new F<MutationOperator, String>() {
            public String apply(MutationOperator operator) {
                return operator.getID();
            }
        });
    }

    public boolean mayMutateClass(final String className) {
        return !FCollection.contains(logginClasses, new Predicate<String>() {
            public Boolean apply(String prefix) {
                return className.startsWith(prefix);
            }
        });
    }

    public Collection<MutationOperator> getOperatorsFor(final Method method) {
        if(excludedMethods.apply(method))
            return new ArrayList<MutationOperator>(0);
        return FCollection.filter(operators, new Predicate<MutationOperator>() {
            public Boolean apply(MutationOperator operator) {
                return operator.canMutate(method);
            }
        });
    }

    @Override
    public String toString() {
        return "DescartesMutationEngine"; //TODO: Add more details here
    }

}
