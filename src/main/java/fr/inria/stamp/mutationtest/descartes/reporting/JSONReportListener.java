package fr.inria.stamp.mutationtest.descartes.reporting;

import java.io.IOException;
import java.util.Collection;

import org.pitest.mutationtest.*;

import org.pitest.functional.Option;
import org.pitest.coverage.TestInfo;
import org.pitest.mutationtest.engine.MutationDetails;
import org.pitest.util.ResultOutputStrategy;
import org.pitest.util.Unchecked;



public class JSONReportListener implements MutationResultListener{

    ResultOutputStrategy strategy;
    Collection<String> mutators;
    long startTime;



    JSONWriter report;

    public JSONReportListener(final long startTime, final Collection<String> mutators, final ResultOutputStrategy strategy) {
        this.strategy = strategy;
        this.startTime = startTime;
        this.mutators = mutators;
    }

    public void runStart() {
        try {
            report = new JSONWriter(strategy.createWriterForFile("mutations.json"));
            report.beginObject();
            report.beginListAttribute("mutators");
            for (String mutator :
                    mutators) {
                report.write(mutator);
            }
            report.endList();
            report.beginListAttribute("mutations");
        }catch(IOException exc) {
            throw Unchecked.translateCheckedException(exc);
        }
    }


    public void handleMutationResult(ClassMutationResults classMutationResults) {
        try {

            for (MutationResult result :
                    classMutationResults.getMutations()) {

                MutationDetails details = result.getDetails();
                DetectionStatus status = result.getStatus();
                Option<String> killer = result.getKillingTest();
                MutationStatusTestPair pair = result.getStatusTestPair();
                String method = details.getMethod().name();
                String methodDescription = details.getId().getLocation().getMethodDesc();


                report.beginObject();
                report.writeAttribute("detected", status.isDetected());
                report.writeAttribute("status", status.name());
                report.writeAttribute("mutator", details.getMutator());

                report.beginObjectAttribute("method");
                report.writeAttribute("name", method);
                report.writeAttribute("description", methodDescription);
                report.writeAttribute("class", details.getClassName().getNameWithoutPackage().asJavaName());
                report.writeAttribute("package", details.getClassName().getPackage().asJavaName());
                report.endObject();

                report.beginObjectAttribute("tests");
                report.writeAttribute("killer", killer.hasSome() ? killer.value() : "");
                report.writeAttribute("run", result.getNumberOfTestsRun());

                report.beginListAttribute("ordered");
                for (TestInfo info :
                        details.getTestsInOrder()) {
                    report.write(info.getName());
                }
                report.endList();

                report.endObject();

                report.endObject();

            }
        }
        catch (IOException exc) {
            throw Unchecked.translateCheckedException(exc);
        }
    }

    public void runEnd() {
        try {
            report.endList();
            report.writeAttribute("time", System.currentTimeMillis() - startTime);
            report.endObject();
            report.close();
        }catch(IOException exc) {
            throw Unchecked.translateCheckedException(exc);
        }
    }

}
