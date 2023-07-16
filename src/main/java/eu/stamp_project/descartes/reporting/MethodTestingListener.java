package eu.stamp_project.descartes.reporting;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.stamp_project.descartes.reporting.models.ClassReport;
import eu.stamp_project.descartes.reporting.models.MethodReport;
import java.io.IOException;
import java.util.Properties;
import org.pitest.mutationtest.ClassMutationResults;
import org.pitest.mutationtest.ListenerArguments;
import org.pitest.mutationtest.MutationResultListener;
import org.pitest.mutationtest.MutationResultListenerFactory;
import org.pitest.util.Unchecked;

public class MethodTestingListener extends BaseMutationResultListener {

  public MethodTestingListener(final ListenerArguments args) {
    super(args);
  }

  private JsonGenerator generator;

  @Override
  public void runStart() {
    try {
      JsonFactory factory = JsonFactory.builder().build();
      generator = factory.createGenerator(
          createWriterFor("methods.json")
      );
      generator.setCodec(new ObjectMapper(factory));

      generator.writeStartObject();
      generator.writeArrayFieldStart("methods");
    } catch (IOException exc) {
      throw Unchecked.translateCheckedException(exc);
    }
  }

  @Override
  public void handleMutationResult(ClassMutationResults results) {
    ClassReport classReport = new ClassReport(results);
    try {
      for (MethodReport methodReport : classReport.getMethods()) {
        generator.writeObject(methodReport);
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
      generator.writeObjectField("mutators", getMutatorNames());
      generator.close();
    } catch (IOException exc) {
      throw Unchecked.translateCheckedException(exc);
    }
  }

  public static class Factory implements MutationResultListenerFactory {

    @Override
    public MutationResultListener getListener(Properties props, ListenerArguments args) {
      return new MethodTestingListener(args);
    }

    @Override
    public String name() {
      return "METHODS";
    }

    @Override
    public String description() {
      return "Pseudo and partially-tested methods JSON report";
    }
  }
}
