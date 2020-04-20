package eu.stamp_project.mutationtest.descartes.operators;

import eu.stamp_project.mutationtest.test.input.Calculator;
import eu.stamp_project.mutationtest.test.TestUtils;
import org.junit.Test;
import org.pitest.reloc.asm.commons.Method;
import org.pitest.classinfo.ClassName;

import java.util.Collection;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;

public class ThisMutationOperatorTest {

    @Test
    public void shouldFilterMethods() {
        MutationOperator operator = MutationOperator.fromID("this");
        Collection<String> methods = TestUtils.getMethods(Calculator.class).stream()
                .filter(method -> operator.canMutate(ClassName.fromClass(Calculator.class), method))
                .map(Method::getName).collect(Collectors.toList());
        assertThat(methods, containsInAnyOrder("getClone"));
    }
}