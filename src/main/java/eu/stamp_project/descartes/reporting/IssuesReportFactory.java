package eu.stamp_project.descartes.reporting;

import java.util.Properties;
import org.pitest.mutationtest.ListenerArguments;
import org.pitest.mutationtest.MutationResultListener;
import org.pitest.mutationtest.MutationResultListenerFactory;

public class IssuesReportFactory implements MutationResultListenerFactory {
  @Override
  public MutationResultListener getListener(
      Properties properties, ListenerArguments listenerArguments) {
    return new IssuesReportListener(listenerArguments);
  }

  @Override
  public String name() {
    return "ISSUES";
  }

  @Override
  public String description() {
    return "Pseudo and partially-tested methods HTML report";
  }
}
