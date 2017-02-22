
package fr.inria.stamp.mutationtest.descartes;

import fr.inria.stamp.mutationtest.descartes.codegeneration.MutationClassAdapter;

import org.pitest.reloc.asm.ClassReader;
import org.pitest.reloc.asm.ClassWriter;

import org.pitest.classinfo.ClassByteArraySource;
import org.pitest.classinfo.ClassName;
import org.pitest.functional.Option;
import org.pitest.functional.F;
import org.pitest.mutationtest.engine.*;

import java.util.List;

public class DescartesMutater implements Mutater {


    private final ClassByteArraySource byteSource;
    private final DescartesMutationEngine engine;


    public DescartesMutater(final ClassByteArraySource byteSource, DescartesMutationEngine engine) {
        this.byteSource = byteSource;
        this.engine = engine;
    }



    public List<MutationDetails> findMutations(final ClassName classToMutate) {

        //TODO: Deal with logging classes
        //List<MutationDetails> result = new ArrayList<MutationDetails>();
        //if(engine.mayNotMutateClass(classToMutate)) return result;

        Option<byte[]> classBytes = byteSource.getBytes(classToMutate.asInternalName());
        return classBytes.flatMap( new F<byte[], List<MutationDetails>>() {
            public List<MutationDetails> apply(byte[] bytes) {
                return getMutationPoints(classToMutate, bytes);
            }
        } );

    }

    private List<MutationDetails> getMutationPoints(ClassName className, byte[] classBytes) {
        ClassReader reader = new ClassReader(classBytes);
        MutationPointFinder finder = new MutationPointFinder(className, engine);
        reader.accept(finder, 0);
        return finder.getMutationPoints();
    }

    public Mutant getMutation(MutationIdentifier mID) {
        Option<byte[]> bytes = byteSource.getBytes(mID.getClassName().asJavaName()); //So does the original PIT
        ClassReader reader = new ClassReader(bytes.value());
        //TODO: Original PIT uses specific structures for caching and thus speedup the process
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        //TODO: Check if its feasible to pass only the location
        MutationClassAdapter adapter = new MutationClassAdapter(mID, writer);
        reader.accept(adapter, 0); //TODO: Check options

        //TODO: Mutation details again here, why ins't the parameter MutationIdentifier instead of MutationDetails?
        // do details may change from discovering to mutation?
        // store the mutation details so they can be recovered here instead of recomputing all?
        return new Mutant(new MutationDetails(mID, "", "", 0, 0), writer.toByteArray());
    }

}

