package eu.stamp_project.descartes.operators;

import eu.stamp_project.descartes.annotations.Operator;
import eu.stamp_project.descartes.codemanipulation.MethodInfo;
import org.pitest.reloc.asm.MethodVisitor;
import org.pitest.reloc.asm.Opcodes;
import org.pitest.reloc.asm.Type;

@Operator(identifier = "null", description = "All method instructions replaced by: return null;")
public class NullMutationOperator extends MutationOperator{

    @Override
    public boolean canMutate(MethodInfo method) {
        int target = method.getReturnType().getSort();
        return  target == Type.OBJECT || target == Type.ARRAY;
    }

    @Override
    public void generateCode(MethodInfo method, MethodVisitor mv) {
        assert canMutate(method);

        mv.visitInsn(Opcodes.ACONST_NULL);
        mv.visitInsn(Opcodes.ARETURN);
    }
}
