package eu.stamp_project.test;

import eu.stamp_project.descartes.operators.MutationOperator;
import net.bytebuddy.jar.asm.Type;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.pitest.bytecode.analysis.ClassTree;
import org.pitest.classinfo.ClassName;
import org.pitest.mutationtest.engine.Location;
import org.pitest.mutationtest.engine.MethodName;
import org.pitest.mutationtest.engine.MutationIdentifier;

import java.io.IOException;
import java.lang.reflect.Method;

public class Utils {

    private Utils() {}


    public static MutationIdentifier toMutationIdentifier(Method method, String operator) {
        ClassName className = ClassName.fromClass(method.getDeclaringClass());
        MethodName methodName = MethodName.fromString(method.getName());
        Location location = new Location(className, methodName, Type.getMethodDescriptor(method));
        return new MutationIdentifier(location, 0, operator);
    }

    public static ClassTree getClassTree(Class<?> target) throws IOException {
        ClassReader reader = new org.objectweb.asm.ClassReader(target.getName());
        ClassNode classNode = new ClassNode();
        ClassTree classTree = new ClassTree(classNode);
        reader.accept(classNode, 0);
        return classTree;
    }
}
