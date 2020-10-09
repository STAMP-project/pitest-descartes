package eu.stamp_project.descartes.operators;

import eu.stamp_project.descartes.DescartesMutater;
import org.pitest.classpath.ClassloaderByteArraySource;
import org.pitest.mutationtest.engine.Mutant;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import static eu.stamp_project.test.TestValues.defaultsFor;
import static eu.stamp_project.test.Utils.toMutationIdentifier;

abstract class MutationOperatorTest {

    Class<?> mutate(Method method, MutationOperator operator) {
        DescartesMutater mutater = new DescartesMutater(
                ClassloaderByteArraySource.fromContext(),
                m -> false,
                List.of(operator));
        Mutant mutant = mutater.getMutation(toMutationIdentifier(method, operator.getIdentifier()));
        return load(method.getDeclaringClass().getName(), mutant.getBytes());
    }

    private Class<?> load(String name, byte[] bytes) {
        return new ClassLoader() {
            Class<?> load() {
                return defineClass(name, bytes, 0, bytes.length);
            }
        }.load();
    }

    Object newInstance(Class<?> type) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException { return type.getConstructor().newInstance(); }

    Object invoke(Method method) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Object instance = newInstance(method.getDeclaringClass());
        return method.invoke(instance, defaultsFor(method.getParameterTypes()));
    }

    Object invoke(Class<?> type, Method method) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Method methodToInvoke = type.getDeclaredMethod(method.getName(), method.getParameterTypes());
        Object instance = newInstance(type);
        return methodToInvoke.invoke(instance, defaultsFor(method.getParameterTypes()));
    }
}
