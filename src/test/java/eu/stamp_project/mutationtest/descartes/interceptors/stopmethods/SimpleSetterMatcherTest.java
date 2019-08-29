package eu.stamp_project.mutationtest.descartes.interceptors.stopmethods;

import org.pitest.bytecode.analysis.MethodTree;

public class SimpleSetterMatcherTest extends BaseMethodMatcherTest{
    @Override
    public boolean criterion(MethodTree method) {
        return method.rawNode().name.startsWith("set");
    }

    @Override
    public StopMethodMatcher getMatcher() {
        return StopMethodMatchers.isSimpleSetter();
    }
}
