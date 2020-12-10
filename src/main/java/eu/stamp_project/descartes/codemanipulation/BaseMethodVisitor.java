package eu.stamp_project.descartes.codemanipulation;

import org.pitest.reloc.asm.MethodVisitor;
import static org.pitest.bytecode.ASMVersion.ASM_VERSION;

public abstract class BaseMethodVisitor extends MethodVisitor {
    public BaseMethodVisitor() {
        super(ASM_VERSION);
    }

    public BaseMethodVisitor(MethodVisitor methodVisitor) {
        super(ASM_VERSION, methodVisitor);
    }
}
