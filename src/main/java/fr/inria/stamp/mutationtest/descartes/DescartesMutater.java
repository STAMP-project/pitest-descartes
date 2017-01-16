
package fr.inria.stamp.mutationtest.descartes;

import org.apache.commons.lang.NotImplementedException;
import org.pitest.classinfo.ClassName;
import org.pitest.mutationtest.engine.Mutant;
import org.pitest.mutationtest.engine.Mutater;
import org.pitest.mutationtest.engine.MutationDetails;
import org.pitest.mutationtest.engine.MutationIdentifier;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;


public class DescartesMutater implements Mutater {

    public Mutant getMutation(MutationIdentifier mutationIdentifier) {
        return null;
    }

    public List<MutationDetails> findMutations(ClassName className) {
        throw new NotImplementedException();
    }

}
