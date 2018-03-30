package eu.stamp_project.mutationtest.descartes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Arrays;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import static org.junit.Assert.*;

import eu.stamp_project.mutationtest.test.*;
import eu.stamp_project.mutationtest.descartes.operators.MutationOperator;

import org.pitest.reloc.asm.ClassReader;
import org.pitest.classinfo.ClassName;
import org.pitest.mutationtest.engine.MutationDetails;

import org.pitest.functional.FCollection;
import org.pitest.functional.F;

import java.util.List;


@RunWith(Parameterized.class)
public class MutationPointFinderTest {

    DescartesMutationEngine engine;

    @Before
    public void initialize() {
        Iterable<String> operators = Arrays.asList("void", "11", "(byte)2", "(short)3", "null", "23456L", "'c'", "3.14", "1.2f", "true", "\"string\"");
        engine = new DescartesMutationEngine(FCollection.map(operators, new F<String, MutationOperator>()
        {
            public MutationOperator apply(String id) {
                return MutationOperator.fromID(id);
            }

        }));
    }

    @Parameter
    public String className;

    @Parameter(1)
    public String[] expectedMethods;

    private static String[] shouldFind(String... names) {
        return names;
    }

    private final static String[] NOTHING = new String[0];

    private static String in(Class<?> type) {
        return type.getName();
    }

    @Parameters(name="{index}: Searching mutation points in {0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                {
                    in(Calculator.class),
                    shouldFind(
                            "clear",
                            "getCeiling",
                            "getByte",
                            "getShort",
                            "getScreen",
                            "getClone",
                            "getSquare",
                            "getRandomOperatorSymbol",
                            "add",
                            "getSomething",
                            "isOdd",
                            "getScreen",
                            "getSomeCalculators",
                            "getSomeMore",
                            "getRange")
                },
                { in(AbstractClass.class), shouldFind("voidMethodWithoutParameters") },
                { in(Interface.class), NOTHING },
                { in(StopMethods.class), NOTHING }
        });
    }

    @Test()
    public void shouldFindMutationPoints() {
        try {

            ClassReader reader = new ClassReader(className);
            MutationPointFinder finder = new MutationPointFinder(new ClassName(className), engine);
            reader.accept(finder, 0);
            assertAfterSorting(expectedMethods, getDescriptions(finder.getMutationPoints()));
        }catch(java.io.IOException exc) {
            fail("Unexpected error: " + exc.getMessage());
        }
    }

    private static String[] getDescriptions(Collection<MutationDetails> points) {
        String[] methods = new String[points.size()];
        int index = 0;
        for (MutationDetails details :
                points) {
            methods[index++] = details.getMethod().name();
        }
        return methods;
    }

    private static void assertAfterSorting(String[] expected, String[] actual) {
        Arrays.sort(expected);
        Arrays.sort(actual);
        assertArrayEquals(expected, actual);
    }
}