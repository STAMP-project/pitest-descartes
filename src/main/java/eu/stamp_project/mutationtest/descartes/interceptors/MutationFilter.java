package eu.stamp_project.mutationtest.descartes.interceptors;

import org.pitest.bytecode.analysis.ClassTree;
import org.pitest.bytecode.analysis.MethodTree;
import org.pitest.mutationtest.build.InterceptorType;
import org.pitest.mutationtest.build.MutationInterceptor;
import org.pitest.mutationtest.engine.Mutater;
import org.pitest.mutationtest.engine.MutationDetails;

import java.util.Collection;
import java.util.Optional;

public abstract class MutationFilter implements MutationInterceptor {

    private ClassTree classTree;

    @Override
    public InterceptorType type() {
        return InterceptorType.FILTER;
    }

    @Override
    public void begin(ClassTree classTree) {
        this.classTree = classTree;
    }

    public Optional<MethodTree> getMethod(MutationDetails details) {
        return classTree.method(details.getId().getLocation());
    }

    public ClassTree getCurrentClass() {
        return classTree;
    }

    @Override
    public void end() {
        classTree = null;
    }
}
