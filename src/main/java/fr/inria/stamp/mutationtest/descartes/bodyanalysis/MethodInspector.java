package fr.inria.stamp.mutationtest.descartes.bodyanalysis;

import org.pitest.reloc.asm.*;
import org.pitest.reloc.asm.commons.Method;

import fr.inria.stamp.mutationtest.descartes.MutationPointFinder;

public class MethodInspector extends MethodVisitor {

    private Method method;
    private MutationPointFinder finder;
    private LineCounter lineCounter;
    private StopMethodRecognizer recognizer;

    public MethodInspector(Method method, MutationPointFinder finder) {
        super(Opcodes.ASM5);

        lineCounter = new LineCounter();
        recognizer = new StopMethodRecognizer();
    }

    @Override
    public void visitCode() {

    }

    @Override
    public void visitEnd() {
        if(!recognizer.isOnFinalState()) {
            finder.registerMutations(method, lineCounter.getFirstLine(), lineCounter.getLine());
        }
    }

    @Override
    public void	visitLineNumber(int line, Label start) {
        lineCounter.registerLine(line);
    }

    @Override
    public void	visitInsn(int opcode) {
        recognizer.advance(opcode);
    }

    @Override
    public void	visitIntInsn(int opcode, int operand) {
        recognizer.advance(opcode);
    }

    @Override
    public void	visitJumpInsn(int opcode, Label label) {
        recognizer.advance(opcode);
    }

    @Override
    public void	visitVarInsn(int opcode, int var) {
        recognizer.advance(opcode);
    }

    @Override
    public void	visitTypeInsn(int opcode, String type){
        recognizer.advance(opcode);
    }

    @Override
    public void	visitFieldInsn(int opcode, String owner, String name, String desc) {
        recognizer.advance(opcode);
    }

    @Override
    public void	visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
        recognizer.advance(opcode);
    }

    @Override
    public void	visitIincInsn(int var, int increment) {
        recognizer.advance(Opcodes.IINC);
    }

    @Override
    public void	visitInvokeDynamicInsn(String name, String desc, Handle bsm, Object... bsmArgs) {
        recognizer.advance(Opcodes.INVOKEDYNAMIC);
    }

    @Override
    public void	visitLdcInsn(Object cst) {
        recognizer.advance(Type.getType(cst.getClass()).getOpcode(Opcodes.LDC));
        //TODO: Which opcode should we pass to the recognizer here?
    }

    @Override
    public void	visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
      recognizer.advance(Opcodes.LOOKUPSWITCH);
    }

    @Override
    public void	visitMultiANewArrayInsn(String desc, int dims){
        recognizer.advance(Opcodes.MULTIANEWARRAY);
    }

    @Override
    public void	visitTableSwitchInsn(int min, int max, Label dflt, Label... labels){
        recognizer.advance(Opcodes.TABLESWITCH);
    }

    @Override
    public void	visitTryCatchBlock(Label start, Label end, Label handler, String type){
        recognizer.dontRecognize();
        //TODO: Are ew always interested if there is a catch block? Should we ignore this?
    }

}
