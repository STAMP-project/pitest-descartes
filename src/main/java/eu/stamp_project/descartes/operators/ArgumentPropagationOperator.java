package eu.stamp_project.descartes.operators;

import eu.stamp_project.descartes.annotations.Operator;
import eu.stamp_project.descartes.codemanipulation.MethodInfo;
import org.pitest.reloc.asm.commons.GeneratorAdapter;

@Operator(identifier = "argument", description = "Method is rewritten so it only returns the first argument whose " +
        "type is equal to the return type")
public class ArgumentPropagationOperator extends MutationOperator {
    @Override
    public boolean canMutate(MethodInfo method) {
        return method.getArgumentTypes().contains(method.getReturnType());
    }

    @Override
    protected void generateCode(MethodInfo method, GeneratorAdapter generator) {
        int index = method.getArgumentTypes().indexOf(method.getReturnType());
        generator.loadArg(index);
        generator.returnValue();
    }
}
