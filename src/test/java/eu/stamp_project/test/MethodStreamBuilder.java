package eu.stamp_project.test;

import java.lang.reflect.Method;
import java.sql.Array;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MethodStreamBuilder {

    private final Class<?> methodOwner;
    private final ArrayList<Method> methodsToReturn;
    private final Set<Method> exceptions = new HashSet<>();

    private MethodStreamBuilder(Class<?> methodOwner) {
        Objects.requireNonNull(methodOwner, "Class for method selection can not be null.");
        this.methodOwner = methodOwner;
        methodsToReturn = new ArrayList<>();
    }

    public MethodStreamBuilder method(String name, Class<?>... parameterTypes) throws NoSuchMethodException {
        methodsToReturn.add(methodOwner.getMethod(name, parameterTypes));
        return this;
    }

    public MethodStreamBuilder allDeclaredMethods() {
        methodsToReturn.addAll(List.of(methodOwner.getDeclaredMethods()));
        return this;
    }

    public MethodStreamBuilder except(String name, Class<?>... parameterTypes) throws NoSuchMethodException {
        exceptions.add(methodOwner.getMethod(name, parameterTypes));
        return this;
    }

    public Stream<Method> toStream() { return methodsToReturn.stream().filter(method -> ! exceptions.contains(method)); }

    public static MethodStreamBuilder fromClass(Class<?> methodOwner) { return new MethodStreamBuilder(methodOwner); }

    public static Stream<Method> select(MethodStreamBuilder... selections) {
        Objects.requireNonNull(selections, "Method selection can not be null.");
        if(selections.length == 0)
            return Stream.empty();
        if(selections.length == 1)
            return selections[0].toStream();
        Stream<Method> result = selections[0].toStream();
        for(int i = 1; i < selections.length; i++) {
            result = Stream.concat(result, selections[i].toStream());
        }
        return result;
    }

}
