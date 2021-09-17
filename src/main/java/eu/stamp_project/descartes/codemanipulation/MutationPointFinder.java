package eu.stamp_project.descartes.codemanipulation;

import eu.stamp_project.descartes.operators.MutationOperator;
import org.pitest.classinfo.ClassName;
import org.pitest.mutationtest.engine.Location;
import org.pitest.mutationtest.engine.MutationDetails;
import org.pitest.mutationtest.engine.MutationIdentifier;
import org.pitest.reloc.asm.ClassReader;
import org.pitest.reloc.asm.Label;
import org.pitest.reloc.asm.MethodVisitor;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class MutationPointFinder extends BaseClassVisitor {

    private final Predicate<MethodInfo> excludedMethods;
    private final Collection<MutationOperator> operators;
    private List<MutationDetails> mutationPoints;
    private  ClassName className;
    private String source = null;


    public MutationPointFinder(MutationOperator... operators) {
        this((method) -> false, List.of(operators));
    }

    public MutationPointFinder(Predicate<MethodInfo> excludedMethods, Collection<MutationOperator> operators) {
        super();

        Objects.requireNonNull(excludedMethods, "Excluded methods predicate must not be null");
        this.excludedMethods = excludedMethods;

        Objects.requireNonNull(operators, "Collection of mutation operators can not be null");
        if(operators.isEmpty()) {
            throw new IllegalArgumentException("Collection of mutation operators can not be empty");
        }
        this.operators = operators;
    }

    public List<MutationDetails> findMutationPoints(ClassName className, ClassReader reader) {
        Objects.requireNonNull(className, "Class name must not be null for mutation finding");
        this.className = className;
        mutationPoints = new ArrayList<>();
        reader.accept(this, 0);
        return mutationPoints;
    }


    @Override
    public void visitSource(String source, String debug) { this.source = source; }

    private boolean canNotMutate(MethodInfo method) {
        // Can not mutate abstract, native methods or constructors or excluded methods
        return method.hasNoCode() || method.isConstructor() || excludedMethods.test(method);
    }

    private Set<MutationOperator> getOperatorsFor(MethodInfo method) {
        return operators.stream().filter(op -> op.canMutate(method)).collect(Collectors.toSet());
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {

        MethodInfo method = createMethodInfo(access, name, desc, signature, exceptions);
        if(canNotMutate(method))
            return null;

        final Set<MutationOperator> selectedOperators = getOperatorsFor(method);
        if(selectedOperators.isEmpty())
            return null;

        return new BaseMethodVisitor() {
            LineCounter counter = new LineCounter();
            org.pitest.mutationtest.engine.gregor.MutationContext ctx;

            @Override
            public void visitLineNumber(int line, Label start) { counter.registerLine(line); }

            @Override
            public void visitEnd() {
                if (!counter.empty())
                    registerMutations(method, selectedOperators, counter);
            }
        };
    }

    private void registerMutations(MethodInfo method, Set<MutationOperator> operators, LineCounter lines) {
        Location location = new Location(className, method.getName(), method.getDescriptor());
        Collection<Integer> indexes = lines.getShiftedRange();
        mutationPoints.addAll(
                operators.stream().map(op -> new MutationDetails(
                        new MutationIdentifier(location, indexes, op.getIdentifier()),
                        source,
                        op.getDescription(),
                        lines.getFirstLine(),
                        0 // Top basic block
                )).collect(Collectors.toSet())
        );
    }
}

