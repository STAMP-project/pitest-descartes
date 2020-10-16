package eu.stamp_project.descartes.operators;

import eu.stamp_project.descartes.codemanipulation.MethodInfo;
import org.pitest.reloc.asm.commons.GeneratorAdapter;

public class ThisMutationOperator extends MutationOperator {
    @Override
    public boolean canMutate(MethodInfo method) {
        return !method.isStatic() && method.getOwner().canBeAssignedTo(method.getReturnType());
    }

    @Override
    protected void generateCode(MethodInfo method, GeneratorAdapter generator) {
        generator.loadThis();
        generator.returnValue();
    }
}
