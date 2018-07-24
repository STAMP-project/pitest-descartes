package eu.stamp_project.mutationtest.descartes.operators;

import eu.stamp_project.mutationtest.descartes.DescartesMutationEngine;
import eu.stamp_project.mutationtest.descartes.MutationPointFinder;
import eu.stamp_project.mutationtest.descartes.codegeneration.MutationClassAdapter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.pitest.classinfo.ClassName;
import org.pitest.mutationtest.engine.MutationDetails;
import org.pitest.reloc.asm.ClassReader;
import org.pitest.reloc.asm.ClassWriter;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;

import java.util.function.Predicate;

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
                {"null",     (Predicate<Object>)(x) -> x == null},
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
                {"\"A\"", isEqualTo("A")}

        });
    }

    private static Predicate<Object> isEqualTo(Object value) {
        return (x) -> x.equals(value);
    }

    @Test
    public void shoultWriteMutant() throws Exception {
        String className = "eu.stamp_project.mutationtest.test.Parameterless";

        //Finding mutation points
        DescartesMutationEngine engine = new DescartesMutationEngine(MutationOperator.fromID(operatorID));
        MutationPointFinder finder = new MutationPointFinder(ClassName.fromString(className), engine);
        ClassReader reader = new ClassReader(className);
        reader.accept(finder, 0);

        //Creating mutants
        for (MutationDetails mutationDetails : finder.getMutationPoints()){
            ClassWriter mutantWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
            MutationClassAdapter adapter = new MutationClassAdapter(mutationDetails.getId(), mutantWriter);
            reader.accept(adapter, 0);
            DynamicClassLoader loader = new DynamicClassLoader();
            Class<?>  mutant = loader.defineClass(className, mutantWriter);
            Object mutantInstance = mutant.newInstance();
            Object result = mutant.getDeclaredMethod(mutationDetails.getMethod().name(), null).invoke(mutantInstance);
            assertTrue("Method <" + mutationDetails.getMethod().name() + "> returned a wrong value for mutation operator: " + operatorID, check.test(result));
        }
    }

}
