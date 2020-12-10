package eu.stamp_project.descartes.interceptors;

import eu.stamp_project.descartes.codemanipulation.LineCounter;
import org.objectweb.asm.MethodVisitor;
import org.pitest.bytecode.analysis.MethodTree;
import org.pitest.mutationtest.engine.MutationDetails;

import java.util.Optional;

import static org.pitest.bytecode.ASMVersion.ASM_VERSION;

public class SkipShortMethodsFilter extends MutationFilter {

    private final int threshold;

    public SkipShortMethodsFilter(int threshold) {
        this.threshold = threshold;
    }

    @Override
    public boolean allows(MutationDetails details) {
        Optional<MethodTree> method = getMethod(details);
        if(!method.isPresent())
            return false;
        LineCounter counter = new LineCounter();

        //For some reason this MethodNode is using ASM's packages instead of the relocated PIT ASM's API
        method.get().rawNode().accept(new MethodVisitor(ASM_VERSION) {
            @Override
            public void visitLineNumber(int line, org.objectweb.asm.Label start) {
                counter.registerLine(line);
            }
        });
        return counter.getCount() >= threshold;
    }
}
