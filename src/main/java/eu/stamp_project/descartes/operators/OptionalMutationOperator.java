package eu.stamp_project.descartes.operators;

import eu.stamp_project.descartes.annotations.Operator;
import eu.stamp_project.descartes.codemanipulation.MethodInfo;
import org.pitest.reloc.asm.Type;
import org.pitest.reloc.asm.commons.GeneratorAdapter;
import org.pitest.reloc.asm.commons.Method;

import java.util.Optional;

import static org.pitest.reloc.asm.Type.getType;

@Operator(identifier = "optional", description = "All method instructions replaced by: return Optional.empty();")
public class OptionalMutationOperator extends MutationOperator {

    @Override
    public boolean canMutate(MethodInfo method) {
        return method.getReturnType().equals(getType(Optional.class));
    }

    @Override
    protected void generateCode(MethodInfo method, GeneratorAdapter generator) {
        Type optionalType = Type.getType(Optional.class);
        Method emptyMethod = new Method("empty", "()L" + optionalType.getInternalName() + ";");
        generator.invokeStatic(optionalType, emptyMethod);
        generator.returnValue();
    }
}
