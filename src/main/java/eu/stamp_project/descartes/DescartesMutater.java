
package eu.stamp_project.descartes;

import eu.stamp_project.descartes.codemanipulation.MethodInfo;
import eu.stamp_project.descartes.codemanipulation.MutationClassAdapter;
import eu.stamp_project.descartes.codemanipulation.MutationPointFinder;
import eu.stamp_project.descartes.operators.MutationOperator;
import org.pitest.classinfo.ClassByteArraySource;
import org.pitest.classinfo.ClassName;
import org.pitest.classpath.ClassloaderByteArraySource;
import org.pitest.mutationtest.engine.*;
import org.pitest.reloc.asm.ClassReader;
import org.pitest.reloc.asm.ClassWriter;


import java.util.*;
import java.util.function.Predicate;

public class DescartesMutater implements Mutater {


    private final ClassByteArraySource byteSource;
    private final Predicate<MethodInfo> excludedMethods;
    private final Collection<MutationOperator> operators;


    public DescartesMutater(final ClassByteArraySource byteSource, Predicate<MethodInfo> excludedMethods, Collection<MutationOperator> operators) {
        Objects.requireNonNull(byteSource, "Source to inspect for mutations can not be null");
        this.byteSource = byteSource;

        Objects.requireNonNull(excludedMethods, "Predicate for excluded methods can not be null");
        this.excludedMethods = excludedMethods;

        Objects.requireNonNull(operators, "Collection of mutation operators can not be null");
        this.operators = operators;
    }

    public List<MutationDetails> findMutations(final ClassName classToMutate) {
        Optional<byte[]> classBytes = byteSource.getBytes(classToMutate.asInternalName());
        if(classBytes.isEmpty())
            return Collections.emptyList();
        MutationPointFinder finder = new MutationPointFinder(excludedMethods, operators);
        ClassReader reader = new ClassReader(classBytes.get());
        return finder.findMutationPoints(classToMutate, reader);
    }

    private byte[] createMutant(MutationIdentifier mID) {
        Optional<byte[]> bytes = byteSource.getBytes(mID.getClassName().asJavaName()); //So does the original PIT
        ClassReader reader = new ClassReader(bytes.orElseThrow(
                () -> new IllegalArgumentException("Class " + mID.getClassName().asJavaName() + " not found in the binary source")));
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        MutationClassAdapter adapter = new MutationClassAdapter(mID, writer);
        reader.accept(adapter, 0);
        return writer.toByteArray();
    }

    public Mutant getMutation(MutationIdentifier mID) {
        // We can return a default MutationDetails object as PIT uses the precomputed details for the process.
        // Given this it would be better to have this method receive the MutationDetails instance instead of the MutationIdentifier.
        return new Mutant(new MutationDetails(mID, "", "", 0, 0), createMutant(mID));
    }

}

