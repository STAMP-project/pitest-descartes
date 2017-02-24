package fr.inria.stamp.mutationtest.descartes.operators;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

public class MutationOperatorFactoryTest {

    @Test
    public void shouldCreateVoidMutator() {
        MutationOperator operator = MutationOperatorFactory.fromID("void");
        assertThat(operator, is((MutationOperator) VoidMutationOperator.get()));
    }

    @Test
    public void shouldGetIntegerMutator() {
        int value = 3;
        String id = String.valueOf(value);
        MutationOperator operator = MutationOperatorFactory.fromID(id);
        assertTrue(operator.getClass().equals(ConstantMutationOperator.class));
        assertThat(operator.getID(), is(id));
        Object constant = ((ConstantMutationOperator)operator).getConstant();
        assertTrue(constant.equals(value));
    }

}