package eu.stamp_project.mutationtest.descartes.operators;


import eu.stamp_project.mutationtest.test.input.Calculator;
import eu.stamp_project.mutationtest.test.TestUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.pitest.classinfo.ClassName;
import org.pitest.reloc.asm.commons.Method;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@RunWith(Parameterized.class)
public class MutationOperatorTest {

    private Collection<Method> classMethods;

    @Before
    public void initialize() {
        classMethods = TestUtils.getMethods(Calculator.class);
    }

    @Parameter
    public String operatorID;

    @Parameter(1)
    public Class<?> expectedClass;

    @Parameter(2)
    public String expectedMethod;

    @Parameters(name="{index}: Searching methods with operator: {0}")
    public static Collection<Object[]> parameters() {

        return Arrays.asList( new Object[][]{
                {"void",       VoidMutationOperator.class,        "clear"},
                {"1",          ConstantMutationOperator.class,    "getCeiling"},
                {"(byte)2",    ConstantMutationOperator.class,    "getByte"},
                {"(short)3",   ConstantMutationOperator.class,    "getShort"},
                {"23456L",     ConstantMutationOperator.class,    "getSquare"},
                {"'c'",        ConstantMutationOperator.class,    "getRandomOperatorSymbol"},
                {"3.14",       ConstantMutationOperator.class,    "add"},
                {"1.2f",       ConstantMutationOperator.class,    "getSomething"},
                {"true",       ConstantMutationOperator.class,    "isOdd"},
                {"\"string\"", ConstantMutationOperator.class,    "getScreen"},
                {"new",        NewInstanceMutationOperator.class, "getMultipleCalculators"},
                {"optional",   OptionalMutationOperator.class,    "getOptionalCalculator"}
        });
    }

    @Test
    public void shouldFilterMethods() {
        MutationOperator operator = MutationOperator.fromID(operatorID);
        assertThat("Wrong mutation operator", operator.getClass(), is(equalTo(expectedClass)));

        List<String> targets = classMethods.stream()
                .filter(method -> operator.canMutate(ClassName.fromClass(Calculator.class), method))
                .map(Method::getName)
                .collect(Collectors.toList());

        assertThat(targets, contains(expectedMethod));
    }

}
