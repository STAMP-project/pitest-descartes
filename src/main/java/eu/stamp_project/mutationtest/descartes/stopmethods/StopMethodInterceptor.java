package eu.stamp_project.mutationtest.descartes.stopmethods;

import org.pitest.bytecode.analysis.ClassTree;
import org.pitest.bytecode.analysis.MethodTree;
import org.pitest.mutationtest.build.InterceptorType;
import org.pitest.mutationtest.build.MutationInterceptor;
import org.pitest.mutationtest.engine.Mutater;
import org.pitest.mutationtest.engine.MutationDetails;

import java.util.Collection;
import java.util.stream.Collectors;

public class StopMethodInterceptor implements MutationInterceptor {

    private StopMethodMatcher matcher;

    public StopMethodInterceptor(StopMethodMatcher matcher) {
        this.matcher = matcher;
    }

    @Override
    public InterceptorType type() {
        return InterceptorType.FILTER;
    }

    private ClassTree classTree;

    @Override
    public void begin(ClassTree classTree) {
        this.classTree = classTree;
    }

    @Override
    public Collection<MutationDetails> intercept(Collection<MutationDetails> collection, Mutater mutater) {
        return collection.stream().filter(
                details -> {
                    MethodTree methodTree = classTree.method(details.getId().getLocation()).orElse(null);
                    return methodTree != null && !matcher.matches(classTree, methodTree);
                })
                .collect(Collectors.toList());
    }

    @Override
    public void end() {
        classTree = null;
    }
}
