package eu.stamp_project.mutationtest.descartes.reporting.models;

import org.junit.Test;
import org.pitest.mutationtest.DetectionStatus;

import static eu.stamp_project.mutationtest.descartes.reporting.models.MethodRecordTest.record;
import static org.junit.Assert.fail;
import static org.pitest.mutationtest.DetectionStatus.*;

public class InvalidMethodRecordTest {

    @Test
    public void shouldNotBeValid() {
        DetectionStatus[][] statuses = new DetectionStatus[][] {
                {KILLED, KILLED, NO_COVERAGE},
                {KILLED, NO_COVERAGE, KILLED},
                {KILLED, NO_COVERAGE, NO_COVERAGE},
                {KILLED, NO_COVERAGE, SURVIVED},
                {KILLED, SURVIVED, NO_COVERAGE},
                {SURVIVED, KILLED, NO_COVERAGE},
                {SURVIVED, NO_COVERAGE, KILLED},
                {SURVIVED, NO_COVERAGE, NO_COVERAGE},
                {SURVIVED, NO_COVERAGE, SURVIVED},
                {SURVIVED, SURVIVED, NO_COVERAGE},
                {NO_COVERAGE, KILLED, KILLED},
                {NO_COVERAGE, KILLED, NO_COVERAGE},
                {NO_COVERAGE, KILLED, SURVIVED},
                {NO_COVERAGE, NO_COVERAGE, KILLED},
                {NO_COVERAGE, NO_COVERAGE, SURVIVED},
                {NO_COVERAGE, SURVIVED, KILLED},
                {NO_COVERAGE, SURVIVED, NO_COVERAGE},
                {NO_COVERAGE, SURVIVED, SURVIVED},
        };

        for(int i = 0; i < statuses.length; i++) {
            try {
                record(statuses[i]);
                fail("Exceptions not thrown.");
            }
            catch (IllegalArgumentException exc){
                continue;
            }
            catch (Throwable exc) {
                fail("Obtained wrong exception: " + exc.getClass().getName());
            }
        }

    }
}