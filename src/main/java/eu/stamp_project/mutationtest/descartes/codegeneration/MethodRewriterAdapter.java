package eu.stamp_project.mutationtest.descartes.codegeneration;

import org.pitest.reloc.asm.AnnotationVisitor;
import org.pitest.reloc.asm.MethodVisitor;
import org.pitest.reloc.asm.Opcodes;

import org.pitest.reloc.asm.TypePath;
import org.pitest.reloc.asm.Label;
import org.pitest.reloc.asm.Handle;

/**
 * Provides a base class for method rewriting adapters.
 * Derived classes should override visitCode method
 */
public abstract class MethodRewriterAdapter extends MethodVisitor {

    public MethodRewriterAdapter() { super(Opcodes.ASM5); }

    public MethodRewriterAdapter(MethodVisitor mv) { super(Opcodes.ASM5, mv); }

    @Override
    public abstract void visitCode();

    @Override
    public void	visitFieldInsn(int opcode, String owner, String name, String desc) { /*Do nothing*/ }

    @Override
    public void	visitIincInsn(int var, int increment) { /*Do nothing*/ }

    @Override
    public void	visitInsn(int opcode) { /*Do nothing*/ }

    @Override
    public AnnotationVisitor visitInsnAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
        return null;
    }

    @Override
    public void	visitIntInsn(int opcode, int operand) { /*Do nothing*/  }

    @Override
    public void	visitInvokeDynamicInsn(String name, String desc, Handle bsm, Object... bsmArgs) { /*Do nothing*/ }

    @Override
    public void	visitJumpInsn(int opcode, Label label) { /*Do nothing*/ }

    @Override
    public void	visitLabel(Label label) { /*Do nothing*/ }

    @Override
    public void	visitLdcInsn(Object cst) { /*Do nothing*/ }

    @Override
    public void	visitLineNumber(int line, Label start) { /*Do nothing*/ }

    @Override
    public void	visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
        /*Do nothing*/
    }

    @Override
    public AnnotationVisitor	visitLocalVariableAnnotation(int typeRef, TypePath typePath, Label[] start, Label[] end, int[] index, String desc, boolean visible) {
        return null;
    }

    @Override
    public void	visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) { /*Do nothing*/ }

    @Override
    public void	visitMaxs(int maxStack, int maxLocals) { /*Do nothing*/ }

    //Deprecated
    //@Override
    //public void	visitMethodInsn(int opcode, String owner, String name, String desc) {}

    @Override
    public void	visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) { /*Do nothing*/ }

    @Override
    public void	visitMultiANewArrayInsn(String desc, int dims){ /*Do nothing*/ }

    @Override
    public void	visitTableSwitchInsn(int min, int max, Label dflt, Label... labels){ /*Do nothing*/ }

    @Override
    public AnnotationVisitor visitTryCatchAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
        return null;
    }

    @Override
    public void	visitTryCatchBlock(Label start, Label end, Label handler, String type){ /*Do nothing*/ }

    @Override
    public void	visitTypeInsn(int opcode, String type){ /*Do nothing*/ }

    @Override
    public void	visitVarInsn(int opcode, int var) { /*Do nothing*/ }

}
