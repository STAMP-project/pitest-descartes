package eu.stamp_project.descartes.interceptors.stopmethods;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.pitest.bytecode.analysis.ClassTree;
import org.pitest.bytecode.analysis.MethodTree;
import org.pitest.mutationtest.engine.Location;
import org.pitest.mutationtest.engine.MethodName;
import org.pitest.reloc.asm.Type;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.stream.Stream;

import static eu.stamp_project.test.Utils.getClassTree;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

abstract class MethodMatcherTest {

    abstract Stream<Method> toIntercept() throws NoSuchMethodException;

    abstract Stream<Method> toAllow() throws NoSuchMethodException;

    abstract StopMethodMatcher getMatcher();

    @TestFactory
    @DisplayName("Matcher should intercept")
    Stream<DynamicTest> testShouldInterceptMethods() throws NoSuchMethodException {
        return toIntercept().map(
                method -> DynamicTest.dynamicTest(
                        method.toGenericString(),
                        () -> assertTrue(matches(method), "Matcher should intercept method " + method.toGenericString())
                )
        );
    }

    @TestFactory
    @DisplayName("Matcher should not intercept")
    Stream<DynamicTest> testShouldNotInterceptMethods() throws NoSuchMethodException {
        return toAllow().map(
                method -> DynamicTest.dynamicTest(
                        method.toGenericString(),
                        () -> assertFalse(matches(method), "Matcher should not intercept method " + method.toGenericString())
                )
        );

    }

    private boolean matches(Method method) throws IOException {
        Class<?> owner = method.getDeclaringClass();
        ClassTree classTree = getClassTree(owner);
        MethodTree methodTree = getMethodTree(classTree, method);
        return getMatcher().matches(classTree, methodTree);
    }

    private MethodTree getMethodTree(ClassTree owner, Method target) {
        Location methodLocation = Location.location(owner.name(), MethodName.fromString(target.getName()), Type.getMethodDescriptor(target));
        Optional<MethodTree> potentialMethodTree = owner.method(methodLocation);
        assertTrue(potentialMethodTree.isPresent(), "Method " + target.toGenericString() + " not found, test can not continue");
        return potentialMethodTree.get();
    }
}
