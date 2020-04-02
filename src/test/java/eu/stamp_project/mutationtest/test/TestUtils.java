package eu.stamp_project.mutationtest.test;

import eu.stamp_project.mutationtest.descartes.DescartesEngineFactory;
import eu.stamp_project.mutationtest.descartes.DescartesMutationEngine;
import eu.stamp_project.mutationtest.descartes.MutationPointFinder;
import eu.stamp_project.mutationtest.descartes.operators.MutationOperator;
import eu.stamp_project.mutationtest.test.input.NativeMethodClass;
import eu.stamp_project.mutationtest.test.input.StopMethods;
import org.junit.Test;
import org.pitest.bytecode.analysis.ClassTree;
import org.pitest.classinfo.ClassName;
import org.pitest.functional.FCollection;
import org.pitest.mutationtest.EngineArguments;
import org.pitest.mutationtest.engine.MutationDetails;
import org.pitest.mutationtest.engine.MutationEngine;
import org.pitest.reloc.asm.ClassReader;
import org.pitest.reloc.asm.commons.Method;
import org.pitest.reloc.asm.tree.ClassNode;
import org.pitest.reloc.asm.tree.MethodNode;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


public class TestUtils {

    public static Collection<Method> getMethods(Class<?> klass) {
        return getMethodNodes(klass)
                .stream()
                .map(methodNode -> new Method(methodNode.name, methodNode.desc))
                .collect(Collectors.toList());
    }

    public static Collection<MethodNode> getMethodNodes(Class<?> klass) {
        try {
            ClassReader reader = new ClassReader(klass.getName());
            ClassNode classNode = new ClassNode();
            reader.accept(classNode, 0);
            return classNode.methods;
        } catch (IOException exc) {
            org.junit.Assert.fail("Unhandled exception: " + exc.getMessage());
            return Collections.emptyList();
        }
    }

    public static byte[] toByteArray(InputStream input) throws IOException {
        byte[] buffer = new byte[4 * 0x400];
        int read;

        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            while ((read = input.read(buffer, 0, buffer.length)) != -1) {
                output.write(buffer, 0, read);
            }
            return output.toByteArray();
        }
    }

    public static Class<?> loadClass(String qualifiedName) throws IOException {
        return loadClass(qualifiedName, qualifiedName.replace('.', '/') + ".class");
    }

    public static Class<?> loadClass(String name, String path) throws IOException {

        ClassLoader parent = TestUtils.class.getClassLoader();
        return new ClassLoader(parent) {
            Class<?> load(String name, String path) throws IOException {
                InputStream input = parent.getResourceAsStream(path);
                assertNotNull("Required " + path + " not found", input);
                byte[] bytes = toByteArray(input);
                return defineClass(name, bytes, 0, bytes.length);
            }
        }.load(name, path);
    }

    public static ClassTree getClassTreeForResource(String path) throws IOException {
        return ClassTree.fromBytes(toByteArray(TestUtils.class.getResourceAsStream(path)));
    }

    public static ClassTree getClassTree(Class<?> target) throws IOException {
        org.objectweb.asm.ClassReader reader = new org.objectweb.asm.ClassReader(target.getName());
        org.objectweb.asm.tree.ClassNode classNode = new org.objectweb.asm.tree.ClassNode();
        ClassTree classTree = new ClassTree(classNode);
        reader.accept(classNode, 0);
        return classTree;
    }

    public static Collection<MutationDetails> findMutationPoints(Class<?> target, String... operators) throws IOException {
        DescartesEngineFactory factory = new DescartesEngineFactory();
        MutationEngine engine = factory.createEngine(EngineArguments.arguments().withMutators(Arrays.asList(operators)));
        String className = target.getName();
        ClassReader reader = new ClassReader(className);
        MutationPointFinder finder = new MutationPointFinder(ClassName.fromString(className), (DescartesMutationEngine) engine);
        reader.accept(finder, 0);
        return finder.getMutationPoints();
    }
}
