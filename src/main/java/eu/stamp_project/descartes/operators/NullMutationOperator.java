package eu.stamp_project.descartes.operators;

import eu.stamp_project.descartes.annotations.Operator;
import eu.stamp_project.descartes.codemanipulation.MethodInfo;
import org.pitest.reloc.asm.Type;
import org.pitest.reloc.asm.commons.GeneratorAdapter;

@Operator(identifier = "null", description = "All method instructions replaced by: return null;")
public class NullMutationOperator extends MutationOperator {

  @Override
  public boolean canMutate(MethodInfo method) {
    int target = method.getReturnType().getSort();
    return target == Type.OBJECT || target == Type.ARRAY;
  }

  @Override
  protected void generateCode(MethodInfo method, GeneratorAdapter generator) {
    generator.push((Type) null);
    generator.returnValue();
  }
}
