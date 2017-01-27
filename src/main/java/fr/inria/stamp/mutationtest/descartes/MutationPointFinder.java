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

class MutationPointFinder extends ClassVisitor {

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
        lastMethod = new Method(name, desc); //TODO: Check abstract and interface methods
        operatorsForLastMethod = engine.getOperatorsFor(lastMethod);

        if(operatorsForLastMethod.size() != 0/* && name.equals("printHelp")*/)
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
        MutationIdentifier id = new MutationIdentifier(location, getRange(start, end), "void");
        return new MutationDetails(id, source, "Fake description", start, 1);
    }

    private MutationDetails getMutationDetails(Method method, MutationOperator operator) {
        Location location = new Location(className, MethodName.fromString(method.getName()), method.getDescriptor());
        //TODO: ID from operator
        //TODO: Get the actual indexes for the whole body of the method. Can be done with a method visitor that counts all instructions
        // index 0 seems to be OK. Its the first index of the method's body
        MutationIdentifier id = new MutationIdentifier(location, 7, "void");
        //TODO: Fill the mutation details
        MutationDetails details = new MutationDetails(id, source, "This is a false description", 8, 9);
        return details;
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
