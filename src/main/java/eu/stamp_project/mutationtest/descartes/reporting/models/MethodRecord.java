package eu.stamp_project.mutationtest.descartes.reporting.models;


import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.util.TraceSignatureVisitor;
import org.pitest.coverage.TestInfo;
import org.pitest.mutationtest.ClassMutationResults;
import org.pitest.mutationtest.DetectionStatus;
import org.pitest.mutationtest.MutationResult;
import org.pitest.mutationtest.engine.Location;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import static java.util.stream.Collectors.toList;

import static eu.stamp_project.mutationtest.descartes.reporting.models.MethodClassification.*;
import static java.util.stream.Collectors.toSet;

public class MethodRecord {

    private Location location;
    private MethodClassification classification;
    private Set<MutationResult> mutationResults = new HashSet<>();

    public MethodRecord(MutationResult mutation) {
        initialize(mutation);
    }

    public MethodRecord(MutationResult... mutations) {
        if(mutations == null || mutations.length == 0)
            throw new IllegalArgumentException("At least one mutation result must be provided.");
        initialize(mutations[0]);
        for (int i = 0; i <  mutations.length; i++)
            add(mutations[i]);
    }

    public MethodRecord(Collection<MutationResult> mutations) {
        this(toArray(mutations));
    }

    public MethodRecord(Stream<MutationResult> mutations) {
        this(mutations.collect(toList()));
    }


    private MethodClassification statusToClassification(DetectionStatus status) {
        if(status.isDetected())
            return TESTED;
        if(status == DetectionStatus.NO_COVERAGE)
            return NOT_COVERED;
        return PSEUDO_TESTED;
    }

    private static MutationResult[] toArray(Collection<MutationResult> mutationResults) {
        MutationResult[] results = new MutationResult[mutationResults.size()];
        mutationResults.toArray(results);
        return results;
    }

    private void initialize(MutationResult mutation) {
        mutationResults.add(mutation);
        location = mutation.getDetails().getId().getLocation();
        classification = statusToClassification(mutation.getStatus());
    }

    private void updateStatus(DetectionStatus status) {
        if(status == DetectionStatus.NO_COVERAGE) {
            if(classification != NOT_COVERED)
                throw new IllegalArgumentException("Method " + location.getMethodName().name() + " has been marked as covered and can not accept a non-covered mutation.");
            return;
        }
        if(classification == NOT_COVERED)
            throw new IllegalArgumentException("Method " + location.getMethodName().name() + " has been marked as non-covered and can not accept a covered mutation");
        if((status.isDetected() && classification == PSEUDO_TESTED)
                || (!status.isDetected() && classification == TESTED))
            classification = PARTIALLY_TESTED;
    }

    public void add(MutationResult mutation) {
        if(!mutation.getDetails().getId().getLocation().equals(location))
            throw new IllegalArgumentException("All related mutations should be placed in the same location");
        mutationResults.add(mutation);
        updateStatus(mutation.getStatus());
    }

    public MethodClassification getClassification() {
        return classification;
    }

    public Collection<TestInfo> getTests() {
        return mutationResults.stream()
                .flatMap( (mutation) -> mutation.getDetails().getTestsInOrder().stream())
                .collect(toSet());
    }

    public Collection<MutationResult> getMutations() {
        return Collections.unmodifiableSet(mutationResults);
    }

    public Collection<MutationResult> getDetectedMutations() {
        return mutationResults.stream().filter(mutation -> mutation.getStatus().isDetected()).collect(toList());
    }

    public Collection<MutationResult> getUndetectedMutations() {
        return mutationResults.stream().filter(mutation -> !mutation.getStatus().isDetected()).collect(toList());
    }

    public Location getLocation() {
        return location;
    }

    public String name() {
        return location.getMethodName().name();
    }

    public String desc() {
        return location.getMethodDesc();
    }

    public String className() {
        return location.getClassName().getNameWithoutPackage().asInternalName();
    }

    public String packageName() {
        return location.getClassName().getPackage().asInternalName();
    }


    public String declaration() {

        TraceSignatureVisitor visitor = new TraceSignatureVisitor(0);
        new SignatureReader(desc()).accept(visitor);
        return name() + visitor.getDeclaration();
    }

    public boolean hasIssues() {
        return classification == PSEUDO_TESTED || classification == PARTIALLY_TESTED;
    }

    public boolean isVoid() {
        return desc().endsWith(")V");
    }

    public static String methodKey(MutationResult mutation) {
        String className = mutation.getDetails().getClassName().asJavaName();
        String methodName = mutation.getDetails().getMethod().name();
        String methodDescription = mutation.getDetails().getId().getLocation().getMethodDesc();

        return className + "." + methodName + methodDescription;
    }

    public static Stream<MethodRecord> getRecords(ClassMutationResults results) {
        return results.getMutations().stream()
                .collect(Collectors.groupingBy(MethodRecord::methodKey))
                .values().stream().map(MethodRecord::new);
    }
}
