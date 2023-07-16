package eu.stamp_project.descartes.reporting;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import java.io.IOException;
import java.util.Collection;
import java.util.Properties;
import java.util.stream.Stream;
import org.pitest.coverage.TestInfo;
import org.pitest.mutationtest.ClassMutationResults;
import org.pitest.mutationtest.DetectionStatus;
import org.pitest.mutationtest.ListenerArguments;
import org.pitest.mutationtest.MutationResult;
import org.pitest.mutationtest.MutationResultListener;
import org.pitest.mutationtest.MutationResultListenerFactory;
import org.pitest.mutationtest.MutationStatusTestPair;
import org.pitest.mutationtest.engine.MutationDetails;
import org.pitest.util.Unchecked;

public class JsonReportListener extends BaseMutationResultListener {

  private JsonGenerator generator;

  public JsonReportListener(final ListenerArguments arguments) {
    super(arguments);
  }

  @Override
  public void runStart() {
    try {
      JsonFactory factory = JsonFactory.builder().build();
      generator = factory.createGenerator(createWriterFor("mutations.json"));
      generator.writeStartObject();
      writeArrayField("mutators", getMutatorNames());
      generator.writeArrayFieldStart("mutations");
    } catch (IOException exc) {
      throw Unchecked.translateCheckedException(exc);
    }
  }

  private void writeArrayField(String field, Stream<String> values) throws IOException {
    generator.writeFieldName(field);
    String[] array = values.toArray(String[]::new);
    generator.writeArray(array, 0, array.length);
  }

  private void writeArrayField(String field, Collection<String> values) throws IOException {
    writeArrayField(field, values.stream());
  }

  @Override
  public void handleMutationResult(ClassMutationResults classMutationResults) {
    try {

      for (MutationResult result : classMutationResults.getMutations()) {

        MutationDetails details = result.getDetails();
        DetectionStatus status = result.getStatus();

        generator.writeStartObject();
        generator.writeBooleanField("detected", status.isDetected());
        generator.writeStringField("status", status.name());
        generator.writeStringField("mutator", details.getMutator());
        generator.writeNumberField("line", details.getLineNumber());

        generator.writeFieldName("blocks");
        int[] blocks = details.getBlocks().stream().mapToInt(Integer::intValue).toArray();
        generator.writeArray(blocks, 0, blocks.length);

        generator.writeStringField("file", details.getFilename());
        generator.writeNumberField("index", details.getFirstIndex()); // Saving only the first index

        generator.writeObjectFieldStart("method");
        generator.writeStringField("name", details.getMethod());
        generator.writeStringField("description", details.getId().getLocation().getMethodDesc());
        generator.writeStringField("class",
            details.getClassName().getNameWithoutPackage().asJavaName());
        generator.writeStringField("package", details.getClassName().getPackage().asJavaName());
        generator.writeEndObject();

        generator.writeObjectFieldStart("tests");
        generator.writeNumberField("run", result.getNumberOfTestsRun());

        writeArrayField("ordered", details.getTestsInOrder().stream().map(TestInfo::getName));

        MutationStatusTestPair pair = result.getStatusTestPair();
        writeArrayField("killing", pair.getKillingTests());
        writeArrayField("succeeding", pair.getSucceedingTests());

        generator.writeEndObject();
        generator.writeEndObject();
      }
    } catch (IOException exc) {
      throw Unchecked.translateCheckedException(exc);
    }
  }

  @Override
  public void runEnd() {
    try {
      generator.writeEndArray();
      generator.writeNumberField("time", getElapsedMilliseconds());
      generator.writeEndObject();
      generator.close();
    } catch (IOException exc) {
      throw Unchecked.translateCheckedException(exc);
    }
  }

  public static class Factory implements MutationResultListenerFactory {

    @Override
    public MutationResultListener getListener(
        Properties properties, ListenerArguments listenerArguments) {
      return new JsonReportListener(listenerArguments);
    }

    @Override
    public String name() {
      return "JSON";
    }

    @Override
    public String description() {
      return "JSON report plugin";
    }
  }
}
