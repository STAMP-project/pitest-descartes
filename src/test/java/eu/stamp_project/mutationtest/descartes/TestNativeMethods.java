package eu.stamp_project.mutationtest.descartes;


import eu.stamp_project.mutationtest.test.NativeMethodClass;
import org.junit.Test;
import org.pitest.classinfo.ClassName;
import org.pitest.mutationtest.EngineArguments;
import org.pitest.mutationtest.engine.MutationEngine;
import org.pitest.reloc.asm.ClassReader;

import java.io.IOException;

import static org.junit.Assert.assertTrue;

public class TestNativeMethods {

    @Test
    public void shouldNotFindNativeMethods() throws IOException  {
        final String className = NativeMethodClass.class.getName();
        DescartesEngineFactory factory = new DescartesEngineFactory();
        MutationEngine engine = factory.createEngine(EngineArguments.arguments());
        ClassReader reader = new ClassReader(className);
        MutationPointFinder finder = new MutationPointFinder(ClassName.fromString(className), (DescartesMutationEngine) engine);
        reader.accept(finder, 0);
        assertTrue(finder.getMutationPoints().isEmpty());
    }
}
