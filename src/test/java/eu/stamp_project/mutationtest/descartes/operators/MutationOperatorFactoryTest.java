package eu.stamp_project.mutationtest.descartes.operators;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

public class MutationOperatorFactoryTest {

    @Test
    public void shouldCreateVoidMutator() {
        MutationOperator operator = MutationOperator.fromID("void");
        assertThat(operator, is((MutationOperator) VoidMutationOperator.getInstance()));
    }

    @Test
    public void shouldCreateNullMutator() {
        MutationOperator operator = MutationOperator.fromID("null");
        assertThat(operator, is((MutationOperator) NullMutationOperator.getInstance()));
    }

    @Test
    public void shouldGetIntegerMutator() {
        int value = 3;
        String id = String.valueOf(value);
        MutationOperator operator = MutationOperator.fromID(id);
        assertTrue(operator.getClass().equals(ConstantMutationOperator.class));
        assertThat(operator.getID(), is(id));
        Object constant = ((ConstantMutationOperator)operator).getConstant();
        assertTrue(constant.equals(value));
    }

}