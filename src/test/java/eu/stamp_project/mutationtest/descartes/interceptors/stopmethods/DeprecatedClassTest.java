package eu.stamp_project.mutationtest.descartes.interceptors.stopmethods;

import org.pitest.bytecode.analysis.MethodTree;
import eu.stamp_project.mutationtest.test.input.DeprecatedClass;

public class DeprecatedClassTest extends BaseMethodMatcherTest {

    @Override
    public boolean criterion(MethodTree method) {
        return true;
    }

    @Override
    public StopMethodMatcher getMatcher() {
        return StopMethodMatchers.isDeprecated();
    }

    @Override
    public Class<?> getTargetClass() { return DeprecatedClass.class; }
}
