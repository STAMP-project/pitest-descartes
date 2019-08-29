package eu.stamp_project.mutationtest.descartes.interceptors.stopmethods;

import org.pitest.bytecode.analysis.MethodTree;

public class SimpleGetterMatcherTest extends BaseMethodMatcherTest {
    @Override
    public boolean criterion(MethodTree method) {
        return  method.rawNode().name.startsWith("get");
    }

    @Override
    public StopMethodMatcher getMatcher() {
        return StopMethodMatchers.isSimpleGetter();
    }
}
