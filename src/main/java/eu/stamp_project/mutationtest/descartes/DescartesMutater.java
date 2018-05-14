
package eu.stamp_project.mutationtest.descartes;

import eu.stamp_project.mutationtest.descartes.codegeneration.MutationClassAdapter;
import org.pitest.classinfo.ClassByteArraySource;
import org.pitest.classinfo.ClassName;
import org.pitest.mutationtest.engine.Mutant;
import org.pitest.mutationtest.engine.Mutater;
import org.pitest.mutationtest.engine.MutationDetails;
import org.pitest.mutationtest.engine.MutationIdentifier;
import org.pitest.reloc.asm.ClassReader;
import org.pitest.reloc.asm.ClassWriter;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class DescartesMutater implements Mutater {


    private final ClassByteArraySource byteSource;
    private final DescartesMutationEngine engine;


    public DescartesMutater(final ClassByteArraySource byteSource, DescartesMutationEngine engine) {
        this.byteSource = byteSource;
        this.engine = engine;
    }

    public List<MutationDetails> findMutations(final ClassName classToMutate) {
        Optional<byte[]> classBytes = byteSource.getBytes(classToMutate.asInternalName());
        if(!classBytes.isPresent())
            return Collections.emptyList();
        return getMutationPoints(classToMutate, classBytes.get());
    }

    private List<MutationDetails> getMutationPoints(ClassName className, byte[] classBytes) {
        ClassReader reader = new ClassReader(classBytes);
        MutationPointFinder finder = new MutationPointFinder(className, engine);
        reader.accept(finder, 0);
        return finder.getMutationPoints();
    }

    public Mutant getMutation(MutationIdentifier mID) {
        Optional<byte[]> bytes = byteSource.getBytes(mID.getClassName().asJavaName()); //So does the original PIT
        ClassReader reader = new ClassReader(bytes.get());
        //TODO: Original PIT uses specific structures for caching and thus speedup the process
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        //TODO: Check if its feasible to pass only the location
        MutationClassAdapter adapter = new MutationClassAdapter(mID, writer);
        reader.accept(adapter, 0);

        //TODO: Mutation details again here, why isn't the parameter MutationIdentifier instead of MutationDetails?
        // do details may change from discovering to mutation?
        // store the mutation details so they can be recovered here instead of recomputing all?
        return new Mutant(new MutationDetails(mID, "", "", 0, 0), writer.toByteArray());
    }

}

