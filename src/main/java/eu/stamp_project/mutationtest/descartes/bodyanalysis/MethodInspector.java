package eu.stamp_project.mutationtest.descartes.bodyanalysis;

import eu.stamp_project.mutationtest.descartes.MutationPointFinder;

import org.pitest.classinfo.ClassName;
import org.pitest.reloc.asm.Label;
import org.pitest.reloc.asm.MethodVisitor;
import org.pitest.reloc.asm.Opcodes;
import org.pitest.reloc.asm.commons.Method;

public class MethodInspector extends MethodVisitor {

    private Method method;
    private MutationPointFinder finder;
    private LineCounter lineCounter;

    public MethodInspector(ClassName className, Method method, MutationPointFinder finder) {
        super(Opcodes.ASM5);

        this.method = method;
        this.finder = finder;
        lineCounter = new LineCounter(className, method);

    }

    @Override
    public void visitEnd() {
        if (!lineCounter.empty()) {
            // Some methods would not include line declaration information
            // we skip those for the moment as they can not be mapped to the original source code.
            finder.registerMutations(method, lineCounter.getFirstLine(), lineCounter.getLastLine());
        }
    }

    @Override
    public void	visitLineNumber(int line, Label start) {
        lineCounter.registerLine(line);
    }

}
