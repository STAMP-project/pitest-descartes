package eu.stamp_project.test.input;

import eu.stamp_project.descartes.annotations.DoNotMutate;

public class SkipMutationOnMethod {
  @DoNotMutate
  public void methodToSkip() {
    throw new IllegalArgumentException();
  }
}
