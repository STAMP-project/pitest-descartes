package eu.stamp_project.mutationtest.descartes.interceptors.stopmethods;

import eu.stamp_project.mutationtest.descartes.interceptors.MutationFilter;
import org.pitest.bytecode.analysis.MethodTree;
import org.pitest.mutationtest.engine.Mutater;
import org.pitest.mutationtest.engine.MutationDetails;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

public class StopMethodInterceptor extends MutationFilter {

    private StopMethodMatcher matcher;

    public StopMethodInterceptor(StopMethodMatcher matcher) {
        this.matcher = matcher;
    }

    @Override
    public Collection<MutationDetails> intercept(Collection<MutationDetails> collection, Mutater mutater) {
        return collection.stream().filter(
                details -> {
                    Optional<MethodTree> methodTree = getMethod(details);
                    return methodTree.isPresent() && !matcher.matches(getCurrentClass(), methodTree.get());
                    })
                .collect(Collectors.toList());
    }
}
