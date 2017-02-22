package fr.inria.stamp.mutationtest.descartes.operators;

import fr.inria.stamp.mutationtest.descartes.DescartesMutationEngine;
import org.junit.Test;
import static org.junit.Assert.*;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import org.pitest.classinfo.ClassName;
import org.pitest.mutationtest.engine.MutationDetails;

import org.pitest.reloc.asm.*;
import java.util.Arrays;
import java.util.Collection;

import fr.inria.stamp.mutationtest.descartes.MutationPointFinder;

@RunWith(Parameterized.class)
public class MutationOperatorTest {

    private static DescartesMutationEngine engine = new DescartesMutationEngine();

    @Parameter
    public String className;

    @Parameter(1)
    public String[] actualMethods;

    @Parameters(name="{index}: {0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                {"fr.inria.stamp.mutationtest.test.Calculator", new String[] {"clear"} },
                {"fr.inria.stamp.mutationtest.test.AbstractClass", new String[] {"voidMethodWithoutParameters"} },
                {"fr.inria.stamp.mutationtest.test.Interface", new String[0]},
        });
    }

    @Test
    public void shouldFindMutationPoints() {

        try{
            ClassReader reader = new ClassReader(className);
            MutationPointFinder finder = new MutationPointFinder(new ClassName(className), engine);
            reader.accept(finder, 0);
            assertAfterSorting(getDescriptions(finder.getMutationPoints()), actualMethods);
        } catch(java.io.IOException exc) {
            fail("Exception thrown: " + exc.getMessage());
        }
    }

    private static String[] getDescriptions(Collection<MutationDetails> points) {
        //Need lambda expressions asap :)
        String[] methods = new String[points.size()];
        int index = 0;
        for (MutationDetails details :
                points) {
            methods[index++] = details.getMethod().name();
        }
        return methods;
    }

    private static void assertAfterSorting(String[] obtained, String[] expected) {
        Arrays.sort(obtained);
        Arrays.sort(expected);
        assertArrayEquals(obtained, expected);
    }
}
