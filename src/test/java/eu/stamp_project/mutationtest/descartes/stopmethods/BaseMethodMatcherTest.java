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

public abstract class BaseMethodMatcherTest {

    public static ClassTree getStopMethodClass() throws IOException {
        ClassReader reader = new ClassReader(StopMethods.class.getName());
        ClassNode classNode = new ClassNode();
        ClassTree classTree = new ClassTree(classNode);
        reader.accept(classNode, 0);
        return classTree;
    }

    public abstract boolean criterion(MethodTree method);

    public abstract StopMethodMatcher getMatcher();

    @Test
    public void shouldMatchMethods() throws IOException {
        ClassTree classTree = getStopMethodClass();
        StopMethodMatcher matcher = getMatcher();
        classTree.methods()
                .filter( method -> criterion(method))
                .forEach((Consumer<? super MethodTree>) /*Disambiguation*/
                        (method -> assertTrue("Could not match method: " + method.rawNode().name, matcher.matches(classTree, method))));
    }

}
