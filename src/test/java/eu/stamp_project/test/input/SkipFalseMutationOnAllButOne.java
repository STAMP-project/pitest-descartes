package eu.stamp_project.test.input;

import eu.stamp_project.descartes.annotations.DoNotMutate;

@DoNotMutate(operators = "false")
public class SkipFalseMutationOnAllButOne {

  public boolean returnFalse() {
    return false;
  }

  @DoNotMutate(operators = "true")
  public boolean returnTrue() {
    return true;
  }
}
