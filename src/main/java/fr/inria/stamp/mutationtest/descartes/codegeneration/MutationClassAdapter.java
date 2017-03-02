package fr.inria.stamp.mutationtest.descartes.codegeneration;

import org.pitest.reloc.asm.Opcodes;
import org.pitest.reloc.asm.ClassVisitor;
import org.pitest.reloc.asm.MethodVisitor;
import org.pitest.reloc.asm.commons.Method;

import org.pitest.mutationtest.engine.Location;
import org.pitest.mutationtest.engine.MutationIdentifier;

import fr.inria.stamp.mutationtest.descartes.operators.MutationOperatorFactory;

public class MutationClassAdapter extends ClassVisitor {

    private final MutationIdentifier mID;

    public MutationClassAdapter(MutationIdentifier mID, ClassVisitor cv) {
        super(Opcodes.ASM5, cv);
        this.mID = mID;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor methodVisitor = cv.visitMethod(access, name, desc, signature, exceptions);
        Location location = mID.getLocation();
        if(location.getMethodDesc().equals(desc) && location.getMethodName().name().equals(name)) {
            return new MutationMethodAdapter(
                    MutationOperatorFactory.fromID(mID.getMutator()),
                    new Method(location.getMethodName().name(), location.getMethodDesc()),
                    methodVisitor);
        }
        return methodVisitor;
    }
}
