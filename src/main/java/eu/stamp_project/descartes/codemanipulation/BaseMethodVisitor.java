package eu.stamp_project.descartes.codemanipulation;

import org.pitest.reloc.asm.MethodVisitor;

public abstract class BaseMethodVisitor extends MethodVisitor {
    public BaseMethodVisitor() {
        super(BaseClassVisitor.ASM_API);
    }

    public BaseMethodVisitor(MethodVisitor methodVisitor) {
        super(BaseClassVisitor.ASM_API, methodVisitor);
    }
}
