

package fr.inria.stamp.mutationtest.descartes;

import org.apache.commons.lang.NotImplementedException;
import org.pitest.functional.predicate.Predicate;
import org.pitest.mutationtest.MutationEngineFactory;
import org.pitest.mutationtest.engine.MutationEngine;


import java.util.Collection;

public class DescartesEngineFactory implements MutationEngineFactory{


    public MutationEngine createEngine(final boolean mutateStaticInitializers,
                                       final Predicate<String> excludedMethods,
                                       final Collection<String> loggingClasses,
                                       final Collection<String> mutators,
                                       boolean detectInLinedCode) {

        throw new NotImplementedException();
    }

    public String name() {
        return "descartes";
    }

    public String description() {
        return "Engine for extreme mutation operators";
    }
}
