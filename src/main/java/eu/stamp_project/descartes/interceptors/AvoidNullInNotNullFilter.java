package eu.stamp_project.descartes.interceptors;

import java.util.Optional;
import org.pitest.bytecode.analysis.MethodTree;
import org.pitest.mutationtest.engine.MutationDetails;

public class AvoidNullInNotNullFilter extends MutationFilter {

  public static final String NOT_NULL_ANNOTATION_TYPE_NAME = "Lorg/jetbrains/annotations/NotNull;";

  @Override
  public boolean allows(MutationDetails details) {
    Optional<MethodTree> method = getMethod(details);
    return !"null".equals(details.getMutator()) || (method.isPresent() && canBeNull(method.get()));
  }

  private boolean canBeNull(MethodTree method) {
    return method.annotations().stream()
        .noneMatch(annotation -> NOT_NULL_ANNOTATION_TYPE_NAME.equals(annotation.desc));
  }
}
