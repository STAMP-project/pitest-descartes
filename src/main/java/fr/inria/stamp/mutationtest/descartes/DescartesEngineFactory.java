

package fr.inria.stamp.mutationtest.descartes;

import org.apache.commons.lang.NotImplementedException;
import org.objectweb.asm.commons.Method;
import org.pitest.classinfo.ClassByteArraySource;
import org.pitest.functional.predicate.Predicate;
import org.pitest.mutationtest.MutationEngineFactory;
import org.pitest.mutationtest.engine.Mutater;
import org.pitest.mutationtest.engine.MutationEngine;


import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class DescartesEngineFactory implements MutationEngineFactory{


    /**
     * Creates the engine instance from the given configuration
     * @param mutateStaticInitializers
     * @param excludedMethods
     * @param loggingClasses
     * @param mutators
     * @param detectInLinedCode In our case this can be ignored
     * @return
     */
    public MutationEngine createEngine(final boolean mutateStaticInitializers,
                                       final Predicate<String> excludedMethods,
                                       final Collection<String> loggingClasses,
                                       final Collection<String> mutators,
                                       boolean detectInLinedCode) {
        return new DescartesMutationEngine(getMethodFilter(mutateStaticInitializers, excludedMethods), getLogginClassesSet(loggingClasses));
    }

    private static Predicate<Method> getMethodFilter(final boolean mutateStaticInitializers, final Predicate<String> excludedMethods) {
        if(mutateStaticInitializers)
            return new Predicate<Method>() {
                public Boolean apply(Method method) {
                    return excludedMethods.apply(method.getName());
                }
            };

        return new Predicate<Method>() {
            public Boolean apply(Method method) {
                return method.getName().equals("<clinit>") || excludedMethods.apply(method.getName());
            }
        };
    }

    private static Set<String> getLogginClassesSet(Collection<String> logginClasses) {
        return new HashSet<String>(logginClasses);
    }





    public String name() {
        return "descartes";
    }

    public String description() {
        return "Engine for extreme mutation operators";
    }
}
