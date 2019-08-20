package eu.stamp_project.mutationtest.descartes.operators;

import eu.stamp_project.mutationtest.test.Calculator;
import eu.stamp_project.mutationtest.test.TestUtils;
import org.junit.Test;
import org.pitest.reloc.asm.commons.Method;

import java.util.Collection;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.contains;

public class NewMutationOperatorTest {

    @Test
    public void shouldFilterMethods() {
        java.util.List l;

        MutationOperator operator = MutationOperator.fromID("new");
        Collection<String> methods = TestUtils.getMethods(Calculator.class)
                .stream()
                .filter(operator::canMutate)
                .map(Method::getName)
                .collect(Collectors.toList());
        assertThat(methods, contains("getMultipleCalculators"));
    }

}

