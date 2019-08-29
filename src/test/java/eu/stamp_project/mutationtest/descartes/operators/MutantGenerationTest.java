package eu.stamp_project.mutationtest.descartes.operators;

import eu.stamp_project.mutationtest.descartes.DescartesMutater;
import eu.stamp_project.mutationtest.descartes.DescartesMutationEngine;

import eu.stamp_project.mutationtest.test.input.Parameterless;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.pitest.classinfo.ClassName;
import org.pitest.classpath.ClassloaderByteArraySource;
import org.pitest.mutationtest.engine.Mutant;
import org.pitest.mutationtest.engine.MutationDetails;

import java.lang.reflect.Array;
import java.util.*;

import java.util.function.Predicate;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public class MutantGenerationTest {

    @Parameterized.Parameter
    public String operatorID;

    @Parameterized.Parameter(1)
    public Predicate<Object> check;

    @Parameterized.Parameters(name="{index}: Creating mutants for: {0}")
    public static Collection<Object[]> parameters() {
        return Arrays.asList( new Object[][]{
                {"null",     (Predicate<Object>)Objects::isNull },
                {"-1",       isEqualTo(-1)},
                {"empty",    (Predicate<Object>)(x) -> x.getClass().isArray() && Array.getLength(x) == 0},
                {"1.2f",     isEqualTo(1.2f)},
                {"1.0",      isEqualTo(1.0)},
                {"(short)1", isEqualTo((short)1)},
                {"(byte)1",  isEqualTo((byte)1)},
                {"123L",     isEqualTo(123L)},
                {"true",     isEqualTo(true)},
                {"'\\n'",    isEqualTo('\n')},
                {"\"\"",     isEqualTo("")},
                {"\"A\"",    isEqualTo("A")},
                {"optional", isEqualTo(Optional.empty())},
                {"new",      (Predicate<Object>) Objects::nonNull} // Weak oracle. For the moment we test if the generation does not fail.


        });
    }

    private static Predicate<Object> isEqualTo(Object value) {
        return (x) -> x.equals(value);
    }

    @Test
    public void shoultWriteMutant() throws Exception {

        Class<Parameterless> target = Parameterless.class;

        DescartesMutater mutater = new DescartesMutater(
                new ClassloaderByteArraySource(target.getClassLoader()),
                new DescartesMutationEngine((MutationOperator.fromID(operatorID)))
        );

        // Find mutation points
        List<MutationDetails> mutationPoints = mutater.findMutations(ClassName.fromClass(target));

        // There must be at least one mutation point for each mutation operator
        assertFalse("No mutation point found for mutator: " + operatorID, mutationPoints.isEmpty());

        // For each mutant
        for(MutationDetails mutationDetails: mutationPoints) {
            // Create the mutant
            Mutant mutant = mutater.getMutation(mutationDetails.getId());
            // Get the mutated code
            Class<?> mutatedClass = loadMutant(mutant);
            Object instance = mutatedClass.newInstance();
            // Invoke the mutated method
            Object result = mutatedClass.getDeclaredMethod(mutationDetails.getMethod().name()).invoke(instance);
            // Check the result
            assertTrue("Method <" + mutationDetails.getMethod().name() + "> returned a wrong value for mutation operator: " + operatorID, check.test(result));
        }
    }

    public static Class<?> loadMutant(Mutant mutant) {
        return new ClassLoader() {
            Class<?> define(Mutant mutant) {
                byte[] bytes = mutant.getBytes();
                return defineClass(mutant.getDetails().getClassName().asJavaName(), bytes, 0, bytes.length);
            }
        }.define(mutant);
    }

}
