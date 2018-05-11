package eu.stamp_project.mutationtest.descartes.operators;

import eu.stamp_project.mutationtest.test.Calculator;
import eu.stamp_project.mutationtest.test.TestUtils;
import org.junit.Test;
import org.pitest.reloc.asm.commons.Method;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.isOneOf;

public class NullMutationOperatorTest {

    @Test
    public void shouldFilterMethods() {

        for (Method method : TestUtils.getMethods(Calculator.class)) {
            if(NullMutationOperator.getInstance().canMutate(method))
                assertThat(method.getName(), isOneOf("getScreen", "getClone", "getSomeCalculators", "getSomeMore", "getRange"));

        }

    }

}