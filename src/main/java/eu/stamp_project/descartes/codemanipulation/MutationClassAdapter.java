package eu.stamp_project.descartes.codemanipulation;

import eu.stamp_project.descartes.operators.MutationOperator;
import org.pitest.mutationtest.engine.Location;
import org.pitest.mutationtest.engine.MutationIdentifier;
import org.pitest.reloc.asm.ClassVisitor;
import org.pitest.reloc.asm.MethodVisitor;
import org.pitest.reloc.asm.Type;
import org.pitest.reloc.asm.commons.GeneratorAdapter;
import org.pitest.reloc.asm.commons.Method;

public class MutationClassAdapter extends BaseClassVisitor {

    private final MutationIdentifier mutation;

    public MutationClassAdapter(MutationIdentifier mutation, ClassVisitor cv) {
        super(cv);
        this.mutation = mutation;
    }

    private boolean shouldNotTarget(MethodInfo method) {
        Location location = mutation.getLocation();
        return !location.getMethodDesc().equals(method.getDescriptor()) || !location.getMethodName().name().equals(method.getName());
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {

        MethodVisitor methodVisitor = cv.visitMethod(access, name, desc, signature, exceptions);
        MethodInfo method = createMethodInfo(access, name, desc, signature, exceptions);
        if(shouldNotTarget(method))
            return methodVisitor;

        final MutationOperator operator = MutationOperator.fromID(mutation.getMutator());

        return new MethodRewritingVisitor(methodVisitor) {
            @Override
            protected void generateCode() {
                GeneratorAdapter adapter = new GeneratorAdapter(mv, method.access, method.name, method.getDescriptor());
                operator.writeMutant(method, adapter);
            }
        };
    }
}
