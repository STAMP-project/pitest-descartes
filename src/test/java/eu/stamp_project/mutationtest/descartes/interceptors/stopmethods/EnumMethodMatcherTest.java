package eu.stamp_project.mutationtest.descartes.interceptors.stopmethods;

import eu.stamp_project.mutationtest.test.input.Numbers;
import org.pitest.bytecode.analysis.MethodTree;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class EnumMethodMatcherTest extends BaseMethodMatcherTest{

    Set<String> exclusions = new HashSet<>(Arrays.asList("<init>", "<clinit>", "extraMethod"));

    public boolean criterion(MethodTree method) {
        return !exclusions.contains(method.rawNode().name);
    }

    @Override
    public Class<?> getTargetClass() {
        return Numbers.class;
    }

    @Override
    public StopMethodMatcher getMatcher() {
        return StopMethodMatchers.isEnumGenerated();
    }
}
