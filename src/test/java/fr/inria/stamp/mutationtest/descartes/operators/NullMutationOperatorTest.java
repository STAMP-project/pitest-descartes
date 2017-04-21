package fr.inria.stamp.mutationtest.descartes.operators;

import fr.inria.stamp.mutationtest.test.Calculator;
import fr.inria.stamp.mutationtest.test.TestUtils;
import org.junit.Test;

import org.pitest.reloc.asm.commons.Method;


import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class NullMutationOperatorTest {

    @Test
    public void shouldFilterMethods() {

        for (Method method : TestUtils.getMethods(Calculator.class)) {
            if(NullMutationOperator.getInstance().canMutate(method))
                assertThat(method.getName(), isOneOf("getScreen", "getClone"));

        }

    }

}