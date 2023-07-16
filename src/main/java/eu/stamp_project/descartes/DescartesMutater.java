package eu.stamp_project.descartes;

import eu.stamp_project.descartes.codemanipulation.MethodInfo;
import eu.stamp_project.descartes.codemanipulation.MutationClassAdapter;
import eu.stamp_project.descartes.codemanipulation.MutationPointFinder;
import eu.stamp_project.descartes.operators.MutationOperator;
import java.util.*;
import java.util.function.Predicate;
import org.pitest.classinfo.ClassByteArraySource;
import org.pitest.classinfo.ClassName;
import org.pitest.mutationtest.engine.Mutant;
import org.pitest.mutationtest.engine.Mutater;
import org.pitest.mutationtest.engine.MutationDetails;
import org.pitest.mutationtest.engine.MutationIdentifier;
import org.pitest.reloc.asm.ClassReader;
import org.pitest.reloc.asm.ClassWriter;

public class DescartesMutater implements Mutater {

  private final ClassByteArraySource byteSource;
  private final Predicate<MethodInfo> excludedMethods;
  private final Collection<MutationOperator> operators;

  public DescartesMutater(
      final ClassByteArraySource byteSource,
      final Predicate<MethodInfo> excludedMethods,
      final Collection<MutationOperator> operators) {
    Objects.requireNonNull(byteSource, "Source to inspect for mutations can not be null");
    this.byteSource = byteSource;

    Objects.requireNonNull(excludedMethods, "Predicate for excluded methods can not be null");
    this.excludedMethods = excludedMethods;

    Objects.requireNonNull(operators, "Collection of mutation operators can not be null");
    this.operators = operators;
  }

  public List<MutationDetails> findMutations(final ClassName classToMutate) {
    Optional<byte[]> classBytes = byteSource.getBytes(classToMutate.asInternalName());
    if (!classBytes.isPresent()) {
      return Collections.emptyList();
    }
    MutationPointFinder finder = new MutationPointFinder(excludedMethods, operators);
    ClassReader reader = new ClassReader(classBytes.get());
    return finder.findMutationPoints(classToMutate, reader);
  }

  private byte[] createMutant(MutationIdentifier mutationIdentifier) {
    Optional<byte[]> bytes =
        // As the original PITest code does
        byteSource.getBytes(mutationIdentifier.getClassName().asJavaName());
    ClassReader reader =
        new ClassReader(
            bytes.orElseThrow(
                () ->
                    new IllegalArgumentException(
                        "Class "
                            + mutationIdentifier.getClassName().asJavaName()
                            + " not found in the binary source")));
    ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
    MutationClassAdapter adapter = new MutationClassAdapter(mutationIdentifier, writer);
    reader.accept(adapter, 0);
    return writer.toByteArray();
  }

  public Mutant getMutation(final MutationIdentifier mutationIdentifier) {
    // We can return a default MutationDetails object as PIT uses the precomputed details for the
    // process.
    // Given this it would be better to have this method receive the MutationDetails instance
    // instead of the MutationIdentifier.
    return new Mutant(
        new MutationDetails(
            mutationIdentifier,
            "",
            "",
            0,
            0),
        createMutant(mutationIdentifier));
  }
}
