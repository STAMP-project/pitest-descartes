package eu.stamp_project.mutationtest.descartes.interceptors.stopmethods;


import org.pitest.bytecode.analysis.MethodTree;

public class LambdaMatcherTest extends BaseMethodMatcherTest{
    @Override
    public boolean criterion(MethodTree method) {
        return method.rawNode().name.startsWith("lambda$") && method.isSynthetic();

    }

    @Override
    public StopMethodMatcher getMatcher() {
        return StopMethodMatchers.isLambda();
    }
}
