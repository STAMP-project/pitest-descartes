package eu.stamp_project.descartes.operators;

import eu.stamp_project.descartes.annotations.Operator;
import eu.stamp_project.descartes.codemanipulation.MethodInfo;
import org.pitest.reloc.asm.MethodVisitor;
import org.pitest.reloc.asm.Opcodes;

import java.util.Optional;

import static org.pitest.reloc.asm.Type.getType;

@Operator(identifier = "optional", description = "All method instructions replaced by: return Optional.empty();")
public class OptionalMutationOperator extends MutationOperator {

    @Override
    public boolean canMutate(MethodInfo method) {
        return method.getReturnType().equals(getType(Optional.class));
    }

    @Override
    public void generateCode(MethodInfo method, MethodVisitor mv) {
        String internalName = getType(Optional.class).getInternalName();
        String emptyMethodDescriptor = String.format("()L%s;", internalName);
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, internalName, "empty", emptyMethodDescriptor, false);
        mv.visitInsn(Opcodes.ARETURN);
    }
}
