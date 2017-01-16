
package fr.inria.stamp.mutationtest.descartes;

import java.util.Collection;

import org.apache.commons.lang.NotImplementedException;
import org.pitest.classinfo.ClassByteArraySource;
import org.pitest.mutationtest.engine.MutationEngine;
import org.pitest.mutationtest.engine.Mutater;

import java.util.Collection;

public class DescartesMutationEngine implements  MutationEngine {

    public Mutater createMutator(final ClassByteArraySource byteSource) {
        throw new NotImplementedException();
    }

    public Collection<String> getMutatorNames() {
        throw new NotImplementedException();
    }

    @Override
    public String toString() {
        return "DescartesMutationEngine"; //TODO: Add more details here
    }

}
