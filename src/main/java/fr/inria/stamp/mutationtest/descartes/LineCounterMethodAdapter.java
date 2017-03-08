package fr.inria.stamp.mutationtest.descartes;

import org.pitest.reloc.asm.Label;
import org.pitest.reloc.asm.MethodVisitor;
import org.pitest.reloc.asm.Opcodes;

//TODO: Remove this class. PIT is using only the first line.
class LineCounterMethodAdapter extends MethodVisitor {

    private boolean started = false; //Flag :(

    private int firstLine;
    private int lastLine;
    private int currentLine;

    private MutationPointFinder finder;

    public LineCounterMethodAdapter(MutationPointFinder finder) {
        super(Opcodes.ASM5);
        this.finder = finder;
    }

    @Override
    public void visitLineNumber(int line, Label start) {
        if(!started) {
            firstLine = line;
            started = true;
        }
        currentLine = line;
    }

    @Override
    public void visitEnd() {
        lastLine = currentLine;
        finder.registerMutations(firstLine, lastLine);
    }
}