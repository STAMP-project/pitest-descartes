package eu.stamp_project.mutationtest.descartes.interceptors.stopmethods;

import eu.stamp_project.mutationtest.test.input.StopMethods;
import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.pitest.bytecode.analysis.ClassTree;
import org.pitest.bytecode.analysis.MethodTree;

import java.io.IOException;

import static eu.stamp_project.mutationtest.test.TestUtils.getClassTree;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public abstract class BaseMethodMatcherTest {

    public abstract boolean criterion(MethodTree method);

    public abstract StopMethodMatcher getMatcher();

    public Class<?> getTargetClass() {
        return StopMethods.class;
    }

    @Test
    public void shouldMatchMethods() throws IOException {

        ClassTree classTree = getClassTree(getTargetClass());
        StopMethodMatcher matcher = getMatcher();
        classTree.methods().stream()
                .filter(this::criterion)
                .forEach(
                        method -> assertTrue("Could not match method: " + method.rawNode().name, matcher.matches(classTree, method)));
    }

    @Test
    public void shouldNotMatch() throws IOException {
        ClassTree classTree = getClassTree(getTargetClass());
        StopMethodMatcher matcher = getMatcher();
        classTree.methods().stream()
                .filter( method -> !criterion(method))
                .forEach(
                        method -> assertFalse("Incorrectly matched: " + method.rawNode().name, matcher.matches(classTree, method)));
    }

}
