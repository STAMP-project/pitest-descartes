package fr.inria.stamp.mutationtest.descartes;

import java.util.ArrayList;
import java.util.Collection;

import fr.inria.stamp.mutationtest.descartes.operators.MutationOperator;
import fr.inria.stamp.mutationtest.descartes.operators.VoidMutationOperator;

import org.pitest.classinfo.ClassByteArraySource;
import org.pitest.functional.FCollection;
import org.pitest.mutationtest.engine.MutationEngine;
import org.pitest.mutationtest.engine.Mutater;
import org.pitest.functional.predicate.Predicate;
import org.pitest.functional.predicate.False;

import java.util.Collections;
import java.util.Arrays;
import java.util.Set;

import org.pitest.reloc.asm.commons.Method;

public class DescartesMutationEngine implements  MutationEngine {

    private final Predicate<Method> excludedMethods;

    private final Set<String> logginClasses;

    //TODO: Handle operator creation and selection
    private final Collection<MutationOperator> availableOperators =Arrays.asList(new MutationOperator[] {VoidMutationOperator.get()});

    public DescartesMutationEngine() {
        this(False.<Method>instance(), Collections.<String>emptySet());
    }

    public DescartesMutationEngine(Predicate<Method> excludedMethods, Set<String> logginClasses) {
        this.excludedMethods = excludedMethods;
        this.logginClasses = logginClasses;
    }

    public Mutater createMutator(final ClassByteArraySource byteSource) {
        return new DescartesMutater(byteSource, this);
    }

    public Collection<String> getMutatorNames() {
        return Arrays.asList(new String[]{"void"}); //TODO: Operator handling by identifier
    }

    public boolean mayMutateClass(final String className) {
        return !FCollection.contains(logginClasses, new Predicate<String>() {
            public Boolean apply(String prefix) {
                return className.startsWith(prefix);
            }
        });
    }

//    public boolean mayMutateMethod(Method method) {
//        return !methodFilter.apply(method);
//    }

    public Collection<MutationOperator> getOperatorsFor(final Method method) {
        if(excludedMethods.apply(method))
            return new ArrayList<MutationOperator>(0);
        return FCollection.filter(availableOperators, new Predicate<MutationOperator>() {
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
