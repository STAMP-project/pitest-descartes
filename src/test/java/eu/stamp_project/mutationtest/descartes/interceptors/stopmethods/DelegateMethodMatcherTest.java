package eu.stamp_project.mutationtest.descartes.interceptors.stopmethods;


import org.pitest.bytecode.analysis.MethodTree;

public class DelegateMethodMatcherTest extends BaseMethodMatcherTest{
    @Override
    public boolean criterion(MethodTree method) {
        String name = method.rawNode().name;
        return name.startsWith("delegate") || name.equals("<init>"); //This class has a default constructor wich is in fact a delegation

    }

    @Override
    public StopMethodMatcher getMatcher() {
        return StopMethodMatchers.isDelegate();
    }
}
