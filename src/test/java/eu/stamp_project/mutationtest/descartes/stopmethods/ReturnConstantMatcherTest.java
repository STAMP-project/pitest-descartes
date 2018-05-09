package eu.stamp_project.mutationtest.descartes.stopmethods;

import org.pitest.bytecode.analysis.MethodTree;

public class ReturnConstantMatcherTest extends BaseMethodMatcherTest{
    @Override
    public boolean criterion(MethodTree method) {
        return method.rawNode().name.startsWith("return");
    }

    @Override
    public StopMethodMatcher getMatcher() {
        return StopMethodMatchers.returnsAConstant();
    }
}
