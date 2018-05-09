package eu.stamp_project.mutationtest.descartes.stopmethods;

import eu.stamp_project.mutationtest.test.StopMethods;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import org.pitest.bytecode.analysis.ClassTree;
import org.pitest.bytecode.analysis.MethodTree;



import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;


import static org.junit.Assert.*;


@RunWith(Parameterized.class)
public class StopMethodMatcherTest {

    static ClassTree classTree;

    @BeforeClass
    public static void getClassTree() throws IOException {
        ClassReader reader = new ClassReader(StopMethods.class.getName());
        ClassNode classNode = new ClassNode();
        classTree = new ClassTree(classNode);
        reader.accept(classNode, 0);
    }

    private MethodTree getMethod(String name) {
        return classTree.methods().findFirst( method -> name.equals(method.rawNode().name)).getOrElse(null);
    }

    @Parameters(name = "{index}: Should match {1}")
    public static Collection<Object[]> data() throws IOException {

        return Arrays.asList(new Object[][]{
                {StopMethodMatchers.isEmptyVoid(),  "emptyVoidMethod"},
                {StopMethodMatchers.isDeprecated(), "isDeprecated"},
                {StopMethodMatchers.isToString(), "toString"},
                {StopMethodMatchers.isHashCode(), "hashCode"}
        });

    }

    @Parameter
    public StopMethodMatcher matcher;

    @Parameter(1)
    public String methodName;


    @Test
    public void shouldMatch() {
        assertTrue(matcher.matches(classTree, getMethod(methodName)));
    }
}