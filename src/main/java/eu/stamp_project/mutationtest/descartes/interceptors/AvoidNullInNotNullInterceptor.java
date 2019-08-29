package eu.stamp_project.mutationtest.descartes.interceptors;

import org.pitest.bytecode.analysis.MethodTree;
import org.pitest.mutationtest.engine.Mutater;
import org.pitest.mutationtest.engine.MutationDetails;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

public class AvoidNullInNotNullInterceptor extends MutationFilter {

    @Override
    public Collection<MutationDetails> intercept(Collection<MutationDetails> collection, Mutater mutater) {
        return collection.stream()
                .filter(this::allows)
                .collect(Collectors.toList());
    }

    private boolean allows(MutationDetails details) {
        Optional<MethodTree> method = getMethod(details);
        return !details.getMutator().equals("null") || (method.isPresent() && canBeNull(method.get()));
    }

    private boolean canBeNull(MethodTree method) {
        return method.annotations()
                .stream()
                .noneMatch(anotation -> anotation.desc.equals("Lorg/jetbrains/annotations/NotNull;"));
    }
}
