package eu.stamp_project.descartes.reporting;

import java.util.Properties;
import org.pitest.mutationtest.ListenerArguments;
import org.pitest.mutationtest.MutationResultListener;
import org.pitest.mutationtest.MutationResultListenerFactory;

public class MethodTestingFactory implements MutationResultListenerFactory {

  public MutationResultListener getListener(Properties props, ListenerArguments args) {
    return new MethodTestingListener(args);
  }

  public String name() {
    return "METHODS";
  }

  public String description() {
    return "Pseudo and partially-tested methods JSON report";
  }
}
