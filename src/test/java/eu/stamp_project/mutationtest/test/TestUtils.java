package eu.stamp_project.mutationtest.test;

import org.pitest.functional.FCollection;
import org.pitest.reloc.asm.ClassReader;
import org.pitest.reloc.asm.commons.Method;
import org.pitest.reloc.asm.tree.ClassNode;
import org.pitest.reloc.asm.tree.MethodNode;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;


public class TestUtils {

    public static Collection<Method> getMethods(Class<?> klass) {
        return getMethodNodes(klass)
                .stream()
                .map( methodNode -> new Method(methodNode.name, methodNode.desc))
                .collect(Collectors.toList());
    }

    public static Collection<MethodNode> getMethodNodes(Class<?> klass) {
        try {
            ClassReader reader = new ClassReader(klass.getName());
            ClassNode classNode = new ClassNode();
            reader.accept(classNode, 0);
            return classNode.methods;
        }
        catch(IOException exc) {
            org.junit.Assert.fail("Unhandled exception: " + exc.getMessage());
            return Collections.emptyList();
        }
    }
}
