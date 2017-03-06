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

import fr.inria.stamp.mutationtest.test.*;
import sun.util.resources.uk.CalendarData_uk;

@RunWith(Parameterized.class)
public class MutationOperatorTest {

    private static DescartesMutationEngine engine = new DescartesMutationEngine();

    @Parameter
    public String operatorID;

    @Parameter(1)
    public String className;

    @Parameter(2)
    public String[] actualMethods;

    private static String[] shouldFind(String... names) {
        return names;
    }

    private final static String[] NOTHING = new String[0];

    private static String in(Class<?> type) {
        return type.getName();
    }



    @Parameters(name="{index}: Applying {0} to {1}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                //Calculator
                {"void", in(Calculator.class), shouldFind("clear") },
                {"1", in(Calculator.class), shouldFind("getCeiling") },
                //{"(byte)2", in(Calculator.class), shouldFind("getByte")},
                //{"(short)3", in(Calculator.class), shouldFind("getShort")},
                {"null", in(Calculator.class), shouldFind("getScreen", "getClone")},
                {"23456L", in(Calculator.class), shouldFind("getSquare")},
                {"'c'", in(Calculator.class), shouldFind("getLastOperatorSymbol")},
                {"3.14", in(Calculator.class), shouldFind("add")},
                {"1.2f", in(Calculator.class), shouldFind("getSomething")},
                {"true", in(Calculator.class), shouldFind("isOdd")},
                {"\"string\"", in(Calculator.class), shouldFind("getScreen")},
                //Abstract class
                {"void", in(AbstractClass.class), shouldFind("voidMethodWithoutParameters") },
                {"2", in(AbstractClass.class), NOTHING},
                //Interface
                {"void", in(Interface.class), NOTHING},
                {"3", in(Interface.class), NOTHING},
        });
    }

    @Test()
    public void shouldFindVoidMutationPoints() {
        try {
            DescartesMutationEngine engine = new DescartesMutationEngine(MutationOperator.fromID(operatorID));
            ClassReader reader = new ClassReader(className);
            MutationPointFinder finder = new MutationPointFinder(new ClassName(className), engine);
            reader.accept(finder, 0);
            assertAfterSorting(getDescriptions(finder.getMutationPoints()), actualMethods);
        }catch(java.io.IOException exc) {
            fail("Unexpected error: " + exc.getMessage());
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
