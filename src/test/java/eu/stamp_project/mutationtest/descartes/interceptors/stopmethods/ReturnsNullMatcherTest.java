package eu.stamp_project.mutationtest.descartes.interceptors.stopmethods;

import org.pitest.bytecode.analysis.MethodTree;

public class ReturnsNullMatcherTest extends BaseMethodMatcherTest {
    @Override
    public boolean criterion(MethodTree method) {
        return method.rawNode().name.equals("returnNull");

    }

    @Override
    public StopMethodMatcher getMatcher() {
        return StopMethodMatchers.returnsNull();
    }
}
