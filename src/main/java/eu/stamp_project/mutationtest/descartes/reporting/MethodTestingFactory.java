package eu.stamp_project.mutationtest.descartes.reporting;

import org.pitest.mutationtest.ListenerArguments;
import org.pitest.mutationtest.MutationResultListener;
import org.pitest.mutationtest.MutationResultListenerFactory;

import java.util.Properties;

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
