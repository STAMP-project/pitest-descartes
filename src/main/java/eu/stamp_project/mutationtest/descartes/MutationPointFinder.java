package eu.stamp_project.mutationtest.descartes;

import eu.stamp_project.mutationtest.descartes.bodyanalysis.MethodInspector;
import eu.stamp_project.mutationtest.descartes.operators.MutationOperator;
import org.pitest.classinfo.ClassName;
import org.pitest.mutationtest.engine.Location;
import org.pitest.mutationtest.engine.MethodName;
import org.pitest.mutationtest.engine.MutationDetails;
import org.pitest.mutationtest.engine.MutationIdentifier;
import org.pitest.reloc.asm.ClassVisitor;
import org.pitest.reloc.asm.MethodVisitor;
import org.pitest.reloc.asm.Opcodes;
import org.pitest.reloc.asm.commons.Method;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static eu.stamp_project.utils.Utils.hasFlag;
import static eu.stamp_project.utils.Utils.isConstructor;

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

        // Abstract methods and constructors are not a target.
        // All optional targets has been implemented in the for of features.
        if(hasFlag(access, Opcodes.ACC_ABSTRACT) || isConstructor(name))
            return null;

        Method method = new Method(name, desc);
        //Check if there are available operators for this method
        if(engine.getOperatorsFor(method).isEmpty())
            return null;

        return new MethodInspector(method, this);
    }

    public void registerMutations(Method method, int start, int end) {
        //TODO: Filtering the set of available operators twice. Could this harm running time?
        for (MutationOperator operator :
                engine.getOperatorsFor(method)) {
            mutationPoints.add(getMutationDetails(method, operator, start, end));
        }
    }

    private MutationDetails getMutationDetails(Method method, MutationOperator operator, int start, int end) {

        Location location = new Location(className, MethodName.fromString(method.getName()), method.getDescriptor());
        MutationIdentifier id = new MutationIdentifier(
                location,
                getIndexes(start, end),
                operator.getID());
        return new MutationDetails(id, source, operator.getDescription(), start, 1);
    }

    private Collection<Integer> getIndexes(int start, int end) {
        return IntStream.rangeClosed(1, end - start + 1).boxed().collect(Collectors.toList());
    }

    public List<MutationDetails> getMutationPoints() {
        return  mutationPoints;
    }

}

