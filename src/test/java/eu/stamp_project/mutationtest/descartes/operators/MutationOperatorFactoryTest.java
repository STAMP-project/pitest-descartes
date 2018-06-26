package eu.stamp_project.mutationtest.descartes.operators;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class MutationOperatorFactoryTest {

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

    @Test(expected = WrongOperatorException.class)
    public void shouldNotCreateAnyOperator() {
        MutationOperator.fromID("@");
    }

}