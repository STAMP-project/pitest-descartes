package eu.stamp_project.descartes.reporting;

import java.util.Properties;
import org.pitest.mutationtest.ListenerArguments;
import org.pitest.mutationtest.MutationResultListener;
import org.pitest.mutationtest.MutationResultListenerFactory;

public class JsonReportFactory implements MutationResultListenerFactory {

  public MutationResultListener getListener(
      Properties properties, ListenerArguments listenerArguments) {
    return new JsonReportListener(
        listenerArguments.getStartTime(),
        listenerArguments.getEngine().getMutatorNames(),
        listenerArguments.getOutputStrategy());
  }

  public String name() {
    return "JSON";
  }

  public String description() {
    return "JSON report plugin";
  }
}
