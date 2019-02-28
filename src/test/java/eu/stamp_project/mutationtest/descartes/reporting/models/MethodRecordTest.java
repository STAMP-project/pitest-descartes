package eu.stamp_project.mutationtest.descartes.reporting.models;

import static eu.stamp_project.mutationtest.descartes.reporting.models.MethodClassification.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.pitest.classinfo.ClassName;
import org.pitest.mutationtest.DetectionStatus;
import static org.pitest.mutationtest.DetectionStatus.*;

import org.pitest.mutationtest.MutationResult;
import org.pitest.mutationtest.MutationStatusTestPair;
import org.pitest.mutationtest.engine.Location;
import org.pitest.mutationtest.engine.MethodName;
import org.pitest.mutationtest.engine.MutationDetails;
import org.pitest.mutationtest.engine.MutationIdentifier;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.*;


@RunWith(Parameterized.class)
public class MethodRecordTest {

    public static MutationResult mutation(DetectionStatus status, String mutant) {
        return new MutationResult(
                new MutationDetails(
                        new MutationIdentifier(
                        new Location(
                                ClassName.fromString("AClass"),
                                MethodName.fromString("aMethod"),
                                "()I"),
                                1,
                                mutant),
                        "path/to/file",
                        "Mutation description",
                        1,
                        0),
                new MutationStatusTestPair(1, status, null));
    }

    public static MutationResult detected(String mutant) {
        return mutation(DetectionStatus.KILLED, mutant);
    }

    public static MutationResult survived(String mutant) {
        return mutation(DetectionStatus.SURVIVED, mutant);
    }

    public static MutationResult notCovered(String mutant) {
        return mutation(DetectionStatus.NO_COVERAGE, mutant);
    }

    public static MethodRecord record(DetectionStatus... statuses) {
        MutationResult[] results = new MutationResult[statuses.length];
        for(int i = 0; i < statuses.length; i++)
            results[i] = mutation(statuses[i], Integer.toString(i));
        return record(results);
    }

    public static MethodRecord  record(MutationResult... results) {
        return new MethodRecord(results);
    }

    @Parameterized.Parameters(name="{index}: Should be {0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                {TESTED,           record(KILLED,      KILLED,      KILLED)},
                {PSEUDO_TESTED,    record(SURVIVED,    SURVIVED,    SURVIVED)},
                {PARTIALLY_TESTED, record(KILLED,      SURVIVED,    KILLED)},
                {PARTIALLY_TESTED, record(KILLED,      SURVIVED,    SURVIVED)},
                {PARTIALLY_TESTED, record(SURVIVED,    KILLED,      KILLED)},
                {PARTIALLY_TESTED, record(SURVIVED,    KILLED,      SURVIVED)},
                {PARTIALLY_TESTED, record(KILLED,      KILLED,      SURVIVED)},
                {PARTIALLY_TESTED, record(SURVIVED,    SURVIVED,    KILLED)},
                {NOT_COVERED,      record(NO_COVERAGE, NO_COVERAGE, NO_COVERAGE)}
        });
    }

    @Parameterized.Parameter()
    public MethodClassification classification;

    @Parameterized.Parameter(1)
    public MethodRecord record;


    @Test
    public void shouldHaveTheRightClassification() {
        assertEquals(classification, record.getClassification());
    }

}