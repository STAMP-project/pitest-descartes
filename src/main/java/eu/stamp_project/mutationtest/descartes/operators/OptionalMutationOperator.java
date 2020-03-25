package eu.stamp_project.mutationtest.descartes.operators;

import org.pitest.classinfo.ClassName;
import org.pitest.reloc.asm.MethodVisitor;
import org.pitest.reloc.asm.Opcodes;
import org.pitest.reloc.asm.commons.Method;

import java.util.Optional;

import static org.pitest.reloc.asm.Type.getType;

public class OptionalMutationOperator extends MutationOperator {

    @Override
    public boolean canMutate(Method method) {
        return method.getReturnType().equals(getType(Optional.class));
    }

    @Override
    public boolean canMutate(ClassName className, Method method) {
        return method.getReturnType().equals(getType(Optional.class));
    }
    
    @Override
    public void generateCode(Method method, MethodVisitor mv) {
        String internalName = getType(Optional.class).getInternalName();
        String emptyMethodDescriptor = String.format("()L%s;", internalName);
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, internalName, "empty", emptyMethodDescriptor, false);
        mv.visitInsn(Opcodes.ARETURN);
    }

    @Override
    public String getID() {
        return "optional";
    }

    @Override
    public String getDescription() {
        return "All method instructions replaced by: return Optional.empty();";
    }

}
