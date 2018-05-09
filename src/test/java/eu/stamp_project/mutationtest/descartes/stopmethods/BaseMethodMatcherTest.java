package eu.stamp_project.mutationtest.descartes.stopmethods;

import java.io.IOException;
import java.util.function.Consumer;
import eu.stamp_project.mutationtest.test.StopMethods;

import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.pitest.bytecode.analysis.ClassTree;
import org.pitest.bytecode.analysis.MethodTree;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public abstract class BaseMethodMatcherTest {

    public static ClassTree getClassTree(String name) throws IOException {
        ClassReader reader = new ClassReader(name);
        ClassNode classNode = new ClassNode();
        ClassTree classTree = new ClassTree(classNode);
        reader.accept(classNode, 0);
        return classTree;
    }

    public static ClassTree getClassTree(Class<?> target) throws IOException {
        return getClassTree(target.getName());
    }

    public static ClassTree getStopMethodClass() throws IOException {
        return getClassTree(StopMethods.class);
    }

    public abstract boolean criterion(MethodTree method);


    public abstract StopMethodMatcher getMatcher();

    public Class<?> getTargetClass() {
        return StopMethods.class;
    }

    @Test
    public void shouldMatchMethods() throws IOException {

        ClassTree classTree = getClassTree(getTargetClass());
        StopMethodMatcher matcher = getMatcher();
        classTree.methods()
                .filter(this::criterion)
                .forEach((Consumer<? super MethodTree>) /*Disambiguation*/
                        (method -> assertTrue("Could not match method: " + method.rawNode().name, matcher.matches(classTree, method))));
    }

    @Test
    public void shouldNotMatch() throws IOException {
        ClassTree classTree = getClassTree(getTargetClass());
        StopMethodMatcher matcher = getMatcher();
        classTree.methods()
                .filter( method -> !criterion(method))
                .forEach((Consumer<? super MethodTree>) /*Disambiguation*/
                        (method -> assertFalse("Incorrectly matched: " + method.rawNode().name, matcher.matches(classTree, method))));
    }

}
