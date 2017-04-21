package fr.inria.stamp.mutationtest.test;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import org.pitest.functional.F;
import org.pitest.functional.FCollection;
import org.pitest.reloc.asm.ClassReader;
import org.pitest.reloc.asm.commons.Method;
import org.pitest.reloc.asm.tree.ClassNode;
import org.pitest.reloc.asm.tree.MethodNode;


public class TestUtils {

    public static Collection<Method> getMethods(Class<?> klass) {
        return FCollection.map(getMethodNodes(klass), new F<MethodNode, Method>() {
            public Method apply(MethodNode node) {
                return new Method(node.name, node.desc);
            }
        });
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
