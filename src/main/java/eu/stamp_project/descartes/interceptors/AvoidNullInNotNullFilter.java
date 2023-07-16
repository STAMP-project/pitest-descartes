package eu.stamp_project.descartes.interceptors;

import java.util.Optional;
import org.pitest.bytecode.analysis.MethodTree;
import org.pitest.mutationtest.engine.MutationDetails;

public class AvoidNullInNotNullFilter extends MutationFilter {

  public boolean allows(MutationDetails details) {
    Optional<MethodTree> method = getMethod(details);
    return !details.getMutator().equals("null") || (method.isPresent() && canBeNull(method.get()));
  }

  private boolean canBeNull(MethodTree method) {
    return method.annotations().stream()
        .noneMatch(annotation -> annotation.desc.equals("Lorg/jetbrains/annotations/NotNull;"));
  }
}
