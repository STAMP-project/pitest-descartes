package eu.stamp_project.descartes.operators;

import static eu.stamp_project.test.TestValues.defaultsFor;
import static eu.stamp_project.test.Utils.toMutationIdentifier;

import eu.stamp_project.descartes.DescartesMutater;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import org.pitest.classpath.ClassloaderByteArraySource;
import org.pitest.mutationtest.engine.Mutant;

abstract class MutationOperatorTest {

  Class<?> mutate(Method method, MutationOperator operator) {
    DescartesMutater mutater =
        new DescartesMutater(
            ClassloaderByteArraySource.fromContext(), m -> false, List.of(operator));
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

  Object newInstance(Class<?> type)
      throws NoSuchMethodException, IllegalAccessException, InvocationTargetException,
          InstantiationException {
    return type.getConstructor().newInstance();
  }

  Object invoke(Method method)
      throws NoSuchMethodException, IllegalAccessException, InvocationTargetException,
          InstantiationException {
    return invokeOn(newInstance(method.getDeclaringClass()), method);
  }

  Object invokeOn(Object receiver, Method method)
      throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
    final Method targetMethod =
        receiver.getClass().equals(method.getDeclaringClass())
            ? method
            : getCorrespondingMethod(receiver.getClass(), method);
    return targetMethod.invoke(receiver, defaultsFor(method.getParameterTypes()));
  }

  Method getCorrespondingMethod(Class<?> type, Method query) throws NoSuchMethodException {
    return type.getDeclaredMethod(query.getName(), query.getParameterTypes());
  }

  Object invoke(Class<?> type, Method method)
      throws NoSuchMethodException, IllegalAccessException, InvocationTargetException,
          InstantiationException {
    return invokeOn(newInstance(type), method);
  }
}
