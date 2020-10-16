package eu.stamp_project.descartes.operators;

import eu.stamp_project.descartes.annotations.Operator;
import eu.stamp_project.test.input.Fluent;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.stream.Stream;

import static eu.stamp_project.test.MethodStreamBuilder.fromClass;
import static org.junit.jupiter.api.Assertions.*;


class ThisMutationOperatorTest extends ParameterlessOperatorTest<ThisMutationOperator> {

    @Override
    ThisMutationOperator buildOperator() {
        return new ThisMutationOperator();
    }

    @Override
    Stream<Method> methodsToMutate() throws NoSuchMethodException {
        return fromClass(Fluent.class)
                .method("addOne")
                .method("addTwo")
                .method("addThree")
                .toStream();
    }

    @Override
    Stream<Method> methodsToAvoid() throws NoSuchMethodException {
        return fromClass(Fluent.class)
                .method("createNew")
                .method("getValueAsString")
                .method("createAndGetValue")
                .method("abstractMethod")
                .method("voidMethod")
                .toStream();
    }

    @TestFactory
    Stream<DynamicTest> testMutation() throws NoSuchMethodException {
        return methodsToMutate().map(method ->
            DynamicTest.dynamicTest(method.toGenericString(), () -> {
                assertTrue(checkFluentAnEffect(Fluent.class, method), "Original fluent input method does not have an effect on the receiver. Check the test input");
                Class<?> mutant = mutate(method);
                assertFalse(checkFluentAnEffect(mutant, method), "Mutated method must not have an effect on the receiver");
            })
        );
    }

    private boolean checkFluentAnEffect(Class<?> owner, Method method) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, NoSuchFieldException {
        Object receiver = newInstance(owner);
        int initialValue = owner.getDeclaredField("value").getInt(receiver);
        Object result = invokeOn(receiver, method);
        int finalValue = owner.getDeclaredField("value").getInt(receiver);
        assertEquals(receiver, result,  "Fluent invocation should return the same instance as the receiver");
        return initialValue != finalValue;
    }
}