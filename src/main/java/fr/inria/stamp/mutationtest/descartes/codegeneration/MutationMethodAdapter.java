package fr.inria.stamp.mutationtest.descartes.codegeneration;

import org.pitest.reloc.asm.MethodVisitor;
import org.pitest.reloc.asm.commons.Method;
import fr.inria.stamp.mutationtest.descartes.operators.MutationOperator;


public class MutationMethodAdapter extends MethodRewriterAdapter {

    private final MutationOperator operator;
    private final Method method;

    public MutationMethodAdapter(MutationOperator operator, Method method, MethodVisitor mv) {
        super(mv);
        this.operator = operator;
        this.method = method;
    }

    @Override
    public void visitCode() {
        operator.generateCode(method, mv);
        mv.visitMaxs(0, 0);
    }
}
