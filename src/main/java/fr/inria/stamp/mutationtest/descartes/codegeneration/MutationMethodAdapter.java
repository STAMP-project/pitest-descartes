package fr.inria.stamp.mutationtest.descartes.codegeneration;

import org.pitest.reloc.asm.MethodVisitor;
import fr.inria.stamp.mutationtest.descartes.operators.MutationOperator;


public class MutationMethodAdapter extends MethodRewriterAdapter {

    private final MutationOperator operator;

    public MutationMethodAdapter(MutationOperator operator, MethodVisitor mv) {
        super(mv);
        this.operator = operator;
    }

    @Override
    public void visitCode() {
        operator.generateCode(mv);
        mv.visitMaxs(0, 0);
    }
}
