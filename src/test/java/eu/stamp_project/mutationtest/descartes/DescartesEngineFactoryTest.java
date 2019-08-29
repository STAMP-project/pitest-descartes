package eu.stamp_project.mutationtest.descartes;

import eu.stamp_project.mutationtest.test.input.Calculator;
import eu.stamp_project.mutationtest.test.TestUtils;
import org.junit.Test;
import org.pitest.classinfo.ClassName;
import org.pitest.classpath.ClassloaderByteArraySource;
import org.pitest.mutationtest.EngineArguments;
import org.pitest.mutationtest.engine.Mutater;
import org.pitest.mutationtest.engine.MutationDetails;
import org.pitest.mutationtest.engine.MutationEngine;
import org.pitest.reloc.asm.commons.Method;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.*;



public class DescartesEngineFactoryTest {

    @Test
    public void shouldCreateEngineWithMutators() throws Exception {
        String[] operators = {"void", "3", "null", "\"string\"", "'a'"};
        DescartesEngineFactory factory = new DescartesEngineFactory();
        MutationEngine engine = factory.createEngine( EngineArguments.arguments().withMutators(Arrays.asList(operators)));
        Collection<String> collectedOperators = engine.getMutatorNames();
        assertThat(collectedOperators, hasSize(operators.length));
        assertThat(collectedOperators, contains(operators));
    }

    @Test
    public void shouldIgnoreExcludedMethods() {
        DescartesEngineFactory factory = new DescartesEngineFactory();
        DescartesMutationEngine descartes = (DescartesMutationEngine) factory.createEngine(EngineArguments.arguments().withExcludedMethods(Arrays.asList("*Something")));
        Optional<Method> excluded = TestUtils.getMethods(Calculator.class).stream().filter(m -> m.getName().equals("getSomething")).findFirst();
        assertTrue("Method getSomething not in Calculator class.", excluded.isPresent());
        assertTrue("Obtained operators for an excluded method", descartes.getOperatorsFor(excluded.get()).isEmpty());
    }

    @Test
    public void shouldFindMutatonsAndExclude() {
        DescartesEngineFactory factory = new DescartesEngineFactory();
        MutationEngine engine = factory.createEngine(EngineArguments.arguments().withExcludedMethods(Arrays.asList("get*")));
        Mutater mutater = engine.createMutator(ClassloaderByteArraySource.fromContext());
        List<MutationDetails> mutations = mutater.findMutations(ClassName.fromString("eu.stamp_project.mutationtest.test.input.Calculator"));
        assertEquals(5, mutations.size());
    }

}