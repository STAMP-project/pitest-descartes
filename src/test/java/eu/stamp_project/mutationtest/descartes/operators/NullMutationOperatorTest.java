package eu.stamp_project.mutationtest.descartes.operators;

import eu.stamp_project.mutationtest.test.Calculator;
import eu.stamp_project.mutationtest.test.TestUtils;
import org.junit.Test;
import org.pitest.reloc.asm.commons.Method;

import java.util.Collection;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;

public class NullMutationOperatorTest {

    @Test
    public void shouldFilterMethods() {
        MutationOperator operator = MutationOperator.fromID("null");
        Collection<String> methods = TestUtils.getMethods(Calculator.class)
                .stream()
                .filter(operator::canMutate)
                .map(Method::getName)
                .collect(Collectors.toList());
        assertThat(methods, containsInAnyOrder("getScreen", "getClone", "getSomeCalculators", "getSomeMore", "getRange", "getMultipleCalculators"));
    }
}