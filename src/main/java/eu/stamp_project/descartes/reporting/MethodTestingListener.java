package eu.stamp_project.descartes.reporting;

import eu.stamp_project.descartes.reporting.models.MethodRecord;
import org.pitest.coverage.TestInfo;
import org.pitest.mutationtest.*;

import org.pitest.util.Unchecked;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class MethodTestingListener implements MutationResultListener {

    public MethodTestingListener(final ListenerArguments args) {
        this.args = args;
    }

    private ListenerArguments args;

    public ListenerArguments getArguments() { return args; }

    private JSONWriter report;

    public void runStart() {
        try {
            report = new JSONWriter(args.getOutputStrategy().createWriterForFile("methods.json"));
            report.beginObject();

            report.beginListAttribute("methods");

        }catch(IOException exc) {
            throw Unchecked.translateCheckedException(exc);
        }
    }

    public void handleMutationResult(ClassMutationResults results) {
            MethodRecord.getRecords(results).forEach(this::writeMethod);
    }

    private Collection<String> getMutators(Collection<MutationResult> mutations) {
        return mutations.stream().map(mutation -> mutation.getDetails().getMutator()).collect(Collectors.toList());
    }

    private void writeMethod(MethodRecord method) {
        try {

            report.beginObject();

            report.writeAttribute("name", method.getName());
            report.writeAttribute("description", method.getDesc());
            report.writeAttribute("class", method.getClassName());
            report.writeAttribute("package", method.getPackageName());
            report.writeAttribute("file-name", method.getFileName());
            report.writeAttribute("line-number", method.getLineNumber());


            report.writeAttribute("classification", method.getClassification().toString());
            report.writeStringListAttribute("detected", getMutators(method.getDetectedMutations()));
            report.writeStringListAttribute("not-detected", getMutators(method.getUndetectedMutations()));


            report.writeStringListAttribute(
                    "tests",
                    method.getTests().stream()
                            .map(TestInfo::getName).collect(Collectors.toList()));

            report.beginListAttribute("mutations");
            method.getMutations().stream().forEach(this::writeMutationDetails);
            report.endList();

            report.endObject();
        }
        catch (IOException exc) {
            throw Unchecked.translateCheckedException(exc);
        }

    }

    private void writeMutationDetails(MutationResult mutation) {
        try {

            report.beginObject();

                report.writeAttribute("status", mutation.getStatus().name());
                report.writeAttribute("mutator", mutation.getDetails().getMutator());
                report.writeAttribute("tests-run", mutation.getNumberOfTestsRun());

                report.writeStringListAttribute("tests",
                    mutation.getDetails().getTestsInOrder().stream().map(TestInfo::getName).collect(Collectors.toList()));

                report.writeStringListAttribute("killing-tests", mutation.getKillingTests());
                report.writeStringListAttribute("succeeding-tests", mutation.getSucceedingTests());

            report.endObject();
        }
        catch (IOException exc) {
            throw Unchecked.translateCheckedException(exc);
        }
    }

    public void runEnd() {
        try {

            report.endList();

            report.beginObjectAttribute("analysis");
            report.writeAttribute("time", System.currentTimeMillis() - args.getStartTime());
            report.writeStringListAttribute("mutators", args.getEngine().getMutatorNames());
            report.endObject();

            report.endObject();
            report.close();

        }
        catch(IOException exc) {
            throw Unchecked.translateCheckedException(exc);
        }
    }
}

