package eu.stamp_project.descartes.reporting;

import java.io.IOException;
import java.util.Collection;
import java.util.stream.Collectors;
import org.pitest.coverage.TestInfo;
import org.pitest.mutationtest.*;
import org.pitest.mutationtest.engine.MutationDetails;
import org.pitest.util.ResultOutputStrategy;
import org.pitest.util.Unchecked;

public class JsonReportListener implements MutationResultListener {

  private final ResultOutputStrategy strategy;
  private final Collection<String> mutators;
  private final long startTime;

  private JsonWriter report;

  public JsonReportListener(
      final long startTime,
      final Collection<String> mutators,
      final ResultOutputStrategy strategy) {
    this.strategy = strategy;
    this.startTime = startTime;
    this.mutators = mutators;
  }

  public void runStart() {
    try {
      report = new JsonWriter(strategy.createWriterForFile("mutations.json"));
      report.beginObject();
      report.beginListAttribute("mutators");
      for (String mutator : mutators) {
        report.write(mutator);
      }
      report.endList();
      report.beginListAttribute("mutations");
    } catch (IOException exc) {
      throw Unchecked.translateCheckedException(exc);
    }
  }

  public void handleMutationResult(ClassMutationResults classMutationResults) {
    try {

      for (MutationResult result : classMutationResults.getMutations()) {

        MutationDetails details = result.getDetails();
        DetectionStatus status = result.getStatus();

        report.beginObject();
        report.writeAttribute("detected", status.isDetected());
        report.writeAttribute("status", status.name());
        report.writeAttribute("mutator", details.getMutator());
        report.writeAttribute("line", details.getLineNumber());

        report.beginAttribute("blocks");
        report.beginList();
        for (Integer block : details.getBlocks()) {
          report.write(block);
        }
        report.endList();

        report.writeAttribute("file", details.getFilename());
        report.writeAttribute("index", details.getFirstIndex()); // Saving only the first index

        report.beginObjectAttribute("method");
        report.writeAttribute("name", details.getMethod());
        report.writeAttribute("description", details.getId().getLocation().getMethodDesc());
        report.writeAttribute("class", details.getClassName().getNameWithoutPackage().asJavaName());
        report.writeAttribute("package", details.getClassName().getPackage().asJavaName());
        report.endObject();

        report.beginObjectAttribute("tests");
        report.writeAttribute("run", result.getNumberOfTestsRun());

        report.writeStringListAttribute(
            "ordered",
            details.getTestsInOrder().stream().map(TestInfo::getName).collect(Collectors.toList()));

        MutationStatusTestPair pair = result.getStatusTestPair();
        report.writeStringListAttribute("killing", pair.getKillingTests());
        report.writeStringListAttribute("succeeding", pair.getSucceedingTests());

        report.endObject();

        report.endObject();
      }
    } catch (IOException exc) {
      throw Unchecked.translateCheckedException(exc);
    }
  }

  public void runEnd() {
    try {
      report.endList();
      report.writeAttribute("time", System.currentTimeMillis() - startTime);
      report.endObject();
      report.close();
    } catch (IOException exc) {
      throw Unchecked.translateCheckedException(exc);
    }
  }
}
