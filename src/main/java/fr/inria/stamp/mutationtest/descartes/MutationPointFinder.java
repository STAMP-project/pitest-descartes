package fr.inria.stamp.mutationtest.descartes;

import fr.inria.stamp.mutationtest.descartes.operators.MutationOperator;

import org.pitest.reloc.asm.ClassVisitor;
import org.pitest.reloc.asm.Label;
import org.pitest.reloc.asm.MethodVisitor;
import org.pitest.reloc.asm.Opcodes;
import org.pitest.reloc.asm.commons.Method;

import org.pitest.classinfo.ClassName;
import org.pitest.mutationtest.engine.Location;
import org.pitest.mutationtest.engine.MethodName;
import org.pitest.mutationtest.engine.MutationDetails;
import org.pitest.mutationtest.engine.MutationIdentifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class MutationPointFinder extends ClassVisitor {

    private final ClassName className;
    private String source = null;

    private final DescartesMutationEngine engine;
    private List<MutationDetails> mutationPoints;

    private Collection<MutationOperator> operatorsForLastMethod;
    private Method lastMethod;

    public MutationPointFinder(ClassName className, DescartesMutationEngine engine) {
        super(Opcodes.ASM5);
        this.engine = engine;
        this.className = className;
        mutationPoints = new ArrayList<MutationDetails>();
    }

    @Override
    public void visitSource(String source, String debug) {
        super.visitSource(source, debug);
        this.source = source;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        //Discard abstract methods
        if( (access & Opcodes.ACC_ABSTRACT) == Opcodes.ACC_ABSTRACT )
            return null;

        lastMethod = new Method(name, desc);
        operatorsForLastMethod = engine.getOperatorsFor(lastMethod);

        if(operatorsForLastMethod.size() != 0)
            return new LineCounterMethodAdapter(this);

        return null;
    }

    /**
     *
     * @param start Index of the first instruction of the method
     * @param end Index of the last instruction of the method
     */
    public void registerMutations(int start, int end) {
        for (MutationOperator operator :
                operatorsForLastMethod) {
            mutationPoints.add( getMutationDetails(operator, start, end));
        }
    }

    private MutationDetails getMutationDetails(MutationOperator operator, int start, int end) {
        Location location = new Location(className, MethodName.fromString(lastMethod.getName()), lastMethod.getDescriptor());
        MutationIdentifier id = new MutationIdentifier(location, getRange(start, end), operator.getID());
        return new MutationDetails(id, source, operator.getDescription(), start, 1);
        //TODO: Reduce the number of equivalent mutants created
        // For example, do not mutate empty void methods. This particular checking is not that easy.
        // The visitLineNumber method of MethodVisitor gives the actual lines in the original code.
        // So a method can be empty but can have several empty lines.
        // This detection will require to check the method body and verify that there is only a return instruction.
        // In the case of constant operators maybe we'll need to look for every possible stack top
        // when a return instruction is reached.
        // To implement this operator definition should contain a MethodVisitor that reports the mutation.
    }

    public List<MutationDetails> getMutationPoints() {
        return  mutationPoints;
    }

    //TODO: Move this to utils
    private Collection<Integer> getRange(int start, int end) {
        Integer[] array = new Integer[end - start + 1];
        for (int i=0; i < array.length; i++)
            //array[i] = start + i;
            array[i] = i;
        return Arrays.asList(array);
    }

}

