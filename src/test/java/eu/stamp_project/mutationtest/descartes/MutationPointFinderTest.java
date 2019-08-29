package eu.stamp_project.mutationtest.descartes;

import eu.stamp_project.mutationtest.test.input.AbstractClass;
import eu.stamp_project.mutationtest.test.input.Calculator;
import eu.stamp_project.mutationtest.test.input.Interface;
import org.junit.Test;

import java.io.IOException;
import java.util.Collection;
import java.util.stream.Collectors;

import static eu.stamp_project.mutationtest.test.TestUtils.findMutationPoints;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


public class MutationPointFinderTest {

    private final static String[] OPERATORS = {"void", "11", "(byte)2", "(short)3", "null", "23456L", "'c'", "3.14", "1.2f", "true", "\"string\""};

    private Collection<String> methodsToMutateIn(Class<?> target) throws IOException {
        return findMutationPoints(target, OPERATORS)
                .stream()
                .map(details -> details.getMethod().name())
                .collect(Collectors.toList());
    }

    @Test
    public void shouldFindMutationPointsInRrgularClass() throws IOException {
        assertThat(
                methodsToMutateIn(Calculator.class),
                containsInAnyOrder(
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
                        "getMultipleCalculators",
                        "getOptionalCalculator",
                        "getRange")
        );
    }

    @Test
    public void shouldFindMutationPointsInAbstractClass() throws IOException {
        assertThat(methodsToMutateIn(AbstractClass.class), contains("voidMethodWithoutParameters"));
    }

    @Test
    public void shouldNotFindAnyMutationPointInInterface() throws IOException {
        assertThat(findMutationPoints(Interface.class), is(empty()));
    }
}