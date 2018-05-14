package eu.stamp_project.mutationtest.descartes.reporting;

import org.pitest.mutationtest.ListenerArguments;
import org.pitest.mutationtest.MutationResultListener;
import org.pitest.mutationtest.MutationResultListenerFactory;



import java.util.Properties;

public class IssuesReportFactory implements MutationResultListenerFactory {
    @Override
    public MutationResultListener getListener(Properties properties, ListenerArguments listenerArguments) {
        return new IssuesReportListener(listenerArguments);
    }

    @Override
    public String name() {
        return "ISSUES";
    }

    @Override
    public String description() {
        return "Generates an HTML document containing classes and methods with found testing issues";
    }
}
