package fr.inria.stamp.mutationtest.descartes;

import fr.inria.stamp.mutationtest.descartes.bodyanalysis.MethodInspector;
import fr.inria.stamp.mutationtest.descartes.operators.MutationOperator;

import org.pitest.reloc.asm.ClassVisitor;
import org.pitest.reloc.asm.MethodVisitor;
import org.pitest.reloc.asm.Opcodes;
import org.pitest.reloc.asm.commons.Method;

import org.pitest.classinfo.ClassName;
import org.pitest.mutationtest.engine.Location;
import org.pitest.mutationtest.engine.MethodName;
import org.pitest.mutationtest.engine.MutationDetails;
import org.pitest.mutationtest.engine.MutationIdentifier;

import java.util.ArrayList;
import java.util.List;

import static fr.inria.stamp.utils.Utils.*;

public class MutationPointFinder extends ClassVisitor {

    private final ClassName className;
    private String source = null;

    private final DescartesMutationEngine engine;
    private List<MutationDetails> mutationPoints;

    //Caching
    //private Collection<MutationOperator> operatorsForLastMethod;
    //private Method lastMethod;

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
        //Discard abstract and synhtetic methods. Access level method filter.
        if(hasFlag(access, Opcodes.ACC_ABSTRACT) || hasFlag(access, Opcodes.ACC_SYNTHETIC))
            return null;

        Method method = new Method(name, desc);
        //Check if there are available operators for this method
        if(engine.getOperatorsFor(method).size() == 0)
            return null;

        return new MethodInspector(method, this);
    }

    /**
     *
     * @param start Index of the first instruction of the method
     * @param end Index of the last instruction of the method
     */
    public void registerMutations(Method method, int start, int end) {
        //TODO: Filtering the set of available operators twice. Could this harm running time?
        for (MutationOperator operator :
                engine.getOperatorsFor(method)) {
            mutationPoints.add(getMutationDetails(method, operator, start, end));
        }
    }

    private MutationDetails getMutationDetails(Method method, MutationOperator operator, int start, int end) {
        Location location = new Location(className, MethodName.fromString(method.getName()), method.getDescriptor());
        MutationIdentifier id = new MutationIdentifier(location, getZeroRange(start, end), operator.getID());
        return new MutationDetails(id, source, operator.getDescription(), start, 1); //TODO: check if this start can be changed by 0
    }

    public List<MutationDetails> getMutationPoints() {
        return  mutationPoints;
    }

}

