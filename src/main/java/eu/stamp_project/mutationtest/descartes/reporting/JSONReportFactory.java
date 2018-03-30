package eu.stamp_project.mutationtest.descartes.reporting;

import org.pitest.coverage.CoverageDatabase;
import org.pitest.mutationtest.ListenerArguments;
import org.pitest.mutationtest.MutationResultListener;
import org.pitest.mutationtest.MutationResultListenerFactory;

import java.util.Properties;

public class JSONReportFactory implements MutationResultListenerFactory {

    public MutationResultListener getListener(Properties properties, ListenerArguments listenerArguments) {
        return new JSONReportListener(
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
