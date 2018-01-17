package fr.inria.stamp.mutationtest.descartes.reporting;

import org.pitest.coverage.TestInfo;
import org.pitest.mutationtest.*;
import org.pitest.mutationtest.engine.MutationDetails;
import org.pitest.util.Unchecked;

import java.io.IOException;
import java.util.*;

public class MethodTestingListener implements MutationResultListener {

    public MethodTestingListener(final ListenerArguments args) {
        this.args = args;
    }

    private ListenerArguments args;

    public ListenerArguments getArguments() { return args; }

    private JSONWriter report;

    public void runStart() {
        try {
            report = new JSONWriter(args.getOutputStrategy().createWriterForFile("mutations.json"));
            report.beginObject();

            report.beginListAttribute("methods");

        }catch(IOException exc) {
            throw Unchecked.translateCheckedException(exc);
        }


    }

    private String getMethodKey(MutationResult result) {
        String className = result.getDetails().getClassName().asJavaName();
        String methodName = result.getDetails().getMethod().name();
        String methodDescription = result.getDetails().getId().getLocation().getMethodDesc();

        return className + "." + methodName + methodDescription;
    }

    private HashMap<String, List<MutationResult>> aggregateMethods(ClassMutationResults results) {

        HashMap<String, List<MutationResult>> methodMap = new HashMap<String, List<MutationResult>>();

        for(MutationResult result: results.getMutations()) {
            String key = getMethodKey(result);
            if(methodMap.containsKey(key)) {
                methodMap.get(key).add(result);
            }
            else {
                List<MutationResult> mutationResults = new LinkedList<MutationResult>();
                mutationResults.add(result);
                methodMap.put(key, mutationResults);
            }
        }

        return methodMap;
    }

    public void handleMutationResult(ClassMutationResults results) {
        try {
            for (List<MutationResult> methodResults : aggregateMethods(results).values()) {
                writeMethod(methodResults);
            }
        }
        catch(IOException exc) {
            throw Unchecked.translateCheckedException(exc);
        }
    }


    private void writeMethod(List<MutationResult> methodResults) throws IOException {
        assert methodResults.size() > 0;

        MutationResult first = methodResults.get(0);

        report.beginObject();

        MutationDetails details = first.getDetails();

        report.writeAttribute("name", details.getMethod().name());
        report.writeAttribute("description", details.getId().getLocation().getMethodDesc());
        report.writeAttribute("class", details.getClassName().getNameWithoutPackage().asInternalName());
        report.writeAttribute("package", details.getClassName().getPackage().asInternalName());
        report.writeStringListAttribute("tests", getAllTest(methodResults));

        classifyMethod(methodResults);

        writeMutations(methodResults);

        report.endObject();
    }


    private Set<String> getAllTest(List<MutationResult> methodResults) {
        Set<String> tests = new HashSet<String>();

        for(MutationResult result: methodResults) {
            for(TestInfo test: result.getDetails().getTestsInOrder()) {
                tests.add(test.getName());
            }
        }

        return tests;
    }

    private void classifyMethod(List<MutationResult> methodResults) throws IOException {
        List<String> detected = new LinkedList<String>();
        List<String> notDetected = new LinkedList<String>();
        List<String> notCovered = new LinkedList<String>();

        for(MutationResult result: methodResults) {
            DetectionStatus status = result.getStatus();
            String mutator = result.getDetails().getMutator();
            if(status.isDetected()) {
                detected.add(mutator);
            }
            else if(status.equals(DetectionStatus.NO_COVERAGE)) {
                notCovered.add(mutator);
            }
            else {
                notDetected.add(mutator);
            }
        }

        MethodClassification classification = MethodClassification.TESTED;

        if(notCovered.size() > 0) {
            assert detected.size() == 0; //This only makes sense in descartes but not in gregor.
            assert notDetected.size() == 0;

            classification = MethodClassification.NOT_COVERED;
        }
        else {
            assert notDetected.size() > 0 || detected.size() > 0;

            if(notDetected.size() > 0) {
                classification = MethodClassification.PSEUDO_TESTED;

                if(detected.size() > 0) {
                    classification = MethodClassification.PARTIALLY_TESTED;
                }
            }

        }

        report.writeStringListAttribute("detected", detected);
        if(classification == MethodClassification.NOT_COVERED) {
            report.writeStringListAttribute("not-detected", notCovered);
        }
        else {
            report.writeStringListAttribute("not-detected", notDetected);
        }

        report.writeAttribute("classification", classification.toString());
    }

    private void writeMutations(List<MutationResult> methodResults) throws IOException {
        report.beginListAttribute("mutations");

        for(MutationResult result: methodResults) {
            report.beginObject();

            report.writeAttribute("status", result.getStatus().name());
            report.writeAttribute("mutator", result.getDetails().getMutator());
            report.writeAttribute("tests-run", result.getNumberOfTestsRun());
            report.writeAttribute("detected-by", result.getKillingTest().getOrElse(null));
            report.beginListAttribute("tests");
            for(TestInfo test : result.getDetails().getTestsInOrder())
                report.write(test.getName());
            report.endList();

            report.endObject();
        }

        report.endList();
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

