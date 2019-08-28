package eu.stamp_project.mutationtest.descartes.operators;

import org.junit.Test;


public class MutationOperatorFactoryTest {

    @Test(expected = WrongOperatorException.class)
    public void shouldThrowException() throws Exception {
        MutationOperator.fromID("@");
    }

}