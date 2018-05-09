package eu.stamp_project.mutationtest.descartes.bodyanalysis;

import org.pitest.reloc.asm.*;
import org.pitest.reloc.asm.commons.Method;

import eu.stamp_project.mutationtest.descartes.MutationPointFinder;

public class MethodInspector extends MethodVisitor {

    private Method method;
    private MutationPointFinder finder;
    private LineCounter lineCounter;
    private StopMethodRecognizer recognizer;

    public MethodInspector(Method method, MutationPointFinder finder) {
        super(Opcodes.ASM5);

        this.method = method; //TODO: Verify is not null
        this.finder = finder; //TODO: Verify is not null
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

}
