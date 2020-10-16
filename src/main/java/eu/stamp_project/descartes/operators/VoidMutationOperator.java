package eu.stamp_project.descartes.operators;

import eu.stamp_project.descartes.annotations.Operator;
import eu.stamp_project.descartes.codemanipulation.MethodInfo;
import org.pitest.reloc.asm.Type;
import org.pitest.reloc.asm.commons.GeneratorAdapter;

@Operator(identifier = "void", description = "Removed all instructions in the method")
public final class VoidMutationOperator extends MutationOperator {

    @Override
    public boolean canMutate(MethodInfo method) {
        //TODO: Detect methods that contain only calls to logging classes or System.out --> this should be done in an interceptor
        return !method.isConstructor() && method.getReturnType().equals(Type.VOID_TYPE);
    }

    @Override
    protected void generateCode(MethodInfo method, GeneratorAdapter generator) {
        generator.returnValue();
    }

}
