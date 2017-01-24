package fr.inria.stamp.mutationtest.codegeneration;

import fr.inria.stamp.mutationtest.descartes.operators.MutationOperator;
import org.objectweb.asm.MethodVisitor;


public class MutationMethodAdapter extends MethodRewriterAdapter {

    private final MutationOperator operator;

    public MutationMethodAdapter(MutationOperator operator, MethodVisitor mv) {
        super(mv);
        this.operator = operator;
    }

    @Override
    public void visitCode() {
        operator.generateCode(mv);
    }
}
