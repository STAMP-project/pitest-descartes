package eu.stamp_project.mutationtest.descartes.codegeneration;

import eu.stamp_project.mutationtest.descartes.operators.MutationOperator;
import org.pitest.reloc.asm.MethodVisitor;
import org.pitest.reloc.asm.commons.Method;


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
