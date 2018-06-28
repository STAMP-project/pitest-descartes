package eu.stamp_project.mutationtest.descartes.stopmethods;

import org.pitest.bytecode.analysis.MethodTree;

public class EmptyArrayMatcherTest extends BaseMethodMatcherTest {


    @Override
    public boolean criterion(MethodTree method) {
        final String name  = method.rawNode().name;
        return name.startsWith("empty") && (name.endsWith("Array") || name.endsWith("Matrix"));
    }

    @Override
    public StopMethodMatcher getMatcher() {
        return StopMethodMatchers.returnsAnEmptyArray();
    }
}
