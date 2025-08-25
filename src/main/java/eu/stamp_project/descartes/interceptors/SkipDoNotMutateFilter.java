package eu.stamp_project.descartes.interceptors;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.objectweb.asm.tree.AnnotationNode;
import org.pitest.bytecode.analysis.ClassTree;
import org.pitest.bytecode.analysis.MethodTree;
import org.pitest.mutationtest.engine.MutationDetails;
import org.pitest.reloc.asm.Type;

public class SkipDoNotMutateFilter extends MutationFilter {

  @Override
  public boolean allows(MutationDetails details) {
    Optional<MethodTree> method = getMethod(details);
    return method.isPresent() && mayMutate(method.get(), getCurrentClass(), details.getMutator());
  }

  private boolean mayMutate(MethodTree method, ClassTree owner, String mutator) {
    Optional<AnnotationNode> doNotMutateAnnotation = getDoNotMutateAnnotation(method.annotations());
    if (doNotMutateAnnotation.isPresent()) {
      return isMutationAllowed(doNotMutateAnnotation.get(), mutator);
    }
    doNotMutateAnnotation = getDoNotMutateAnnotation(owner.annotations());
    return !doNotMutateAnnotation.isPresent()
        || isMutationAllowed(doNotMutateAnnotation.get(), mutator);
  }

  private Optional<AnnotationNode> getDoNotMutateAnnotation(List<AnnotationNode> annotations) {
    return annotations.stream()
        .filter(annotation -> Type.getType(annotation.desc).getClassName().endsWith(".DoNotMutate"))
        .findFirst();
  }

  private boolean isMutationAllowed(AnnotationNode annotation, String mutator) {
    Set<String> explicitlyForbiddenMutators = forbiddenMutators(annotation);
    return !explicitlyForbiddenMutators.isEmpty() && !explicitlyForbiddenMutators.contains(mutator);
  }

  private Set<String> forbiddenMutators(AnnotationNode annotation) {
    List<Object> values = annotation.values;
    if (values == null || values.size() < 2) {
      return Collections.emptySet();
    }
    // We only look at the first parameter
    Object parameter = values.get(1);
    if (parameter instanceof String) {
      return Set.of((String) parameter);
    }
    if (parameter instanceof List) {
      Set<String> result = new HashSet<>();
      for (Object val : (List<?>) parameter) {
        if (val instanceof String) {
          result.add((String) val);
        }
      }
      return result;
    }
    return Collections.emptySet();
  }
}
