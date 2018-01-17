package fr.inria.stamp.mutationtest.descartes.reporting;

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
        return "Produces a JSON file reporting mutation results aggregated by method and each method classfied according to its mutation status";
    }
}
