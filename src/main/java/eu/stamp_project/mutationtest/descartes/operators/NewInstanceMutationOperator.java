package eu.stamp_project.mutationtest.descartes.operators;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.pitest.reloc.asm.MethodVisitor;
import org.pitest.reloc.asm.Opcodes;
import org.pitest.reloc.asm.Type;
import org.pitest.reloc.asm.commons.Method;

/**
 * Replaces the method body with a return statement, which generates an instance of the specified return type.
 * <br>
 * This is currently working for classes with parameterless constructors and a few well-known Java interfaces.
 */
public class NewInstanceMutationOperator extends MutationOperator {

    private static final Map<Class<?>, Class<?>> ALTERNATIVE_RETURN_CLASS_BY_TYPE = new HashMap<>();

    static {
        ALTERNATIVE_RETURN_CLASS_BY_TYPE.put(Collection.class, ArrayList.class);
        ALTERNATIVE_RETURN_CLASS_BY_TYPE.put(Iterable.class, ArrayList.class);
        ALTERNATIVE_RETURN_CLASS_BY_TYPE.put(List.class, ArrayList.class);
        ALTERNATIVE_RETURN_CLASS_BY_TYPE.put(Queue.class, LinkedList.class);
        ALTERNATIVE_RETURN_CLASS_BY_TYPE.put(Set.class, HashSet.class);
        ALTERNATIVE_RETURN_CLASS_BY_TYPE.put(Map.class, HashMap.class);
    }

    /**
     * Returns a value indicating whether the operator can transform the given method.
     */
    @Override
    public boolean canMutate(Method method) {
        try {
            Class<?> returnClass = getAppropriateReturnClass(method);

            if(returnClass.equals(String.class)
                    || !belongsToJavaPackages(returnClass)
                    || Modifier.isAbstract(returnClass.getModifiers())) {
                return false;
            }
            for (Constructor<?> publicConstructor : returnClass.getConstructors()) {
                if (publicConstructor.getParameters().length == 0) {
                    System.out.println("Mutating " + returnClass.getName());
                    return true;
                }
            }
            return false;
        }
        catch (ClassNotFoundException e) {
            return false;
        }
    }

    private boolean belongsToJavaPackages(Class<?> aClass) {
        Package classPackage = aClass.getPackage();
        if (classPackage == null) {
            return false;
        }
        String[] nameParts = classPackage.getName().split("\\.");
        return nameParts.length >= 1 && nameParts[0].equals("java");
    }

    /**
     * Returns the specified class or a class which is assignable from this class.
     */
    protected static Class<?> getAppropriateReturnClass(Method method) throws ClassNotFoundException {
        return getAppropriateReturnClass(method.getReturnType().getClassName());
    }

    /**
     * Returns the specified class or a class which is assignable from this class.
     */
    protected static Class<?> getAppropriateReturnClass(String className) throws ClassNotFoundException {
        Class<?> originalClass = Class.forName(className);

        if (ALTERNATIVE_RETURN_CLASS_BY_TYPE.containsKey(originalClass)) {
            return ALTERNATIVE_RETURN_CLASS_BY_TYPE.get(originalClass);
        }

        return originalClass;
    }

    @Override
    public void generateCode(Method method, MethodVisitor mv) {
        assert canMutate(method);

        Class<?> appropriateReturnClass = null;

        try {
            appropriateReturnClass = getAppropriateReturnClass(method);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }

        Type appropriateReturnType = Type.getType(appropriateReturnClass);

        mv.visitTypeInsn(Opcodes.NEW, appropriateReturnType.getInternalName());
        mv.visitInsn(Opcodes.DUP);
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, appropriateReturnType.getInternalName(), "<init>", "()V", false);
        mv.visitInsn(Opcodes.ARETURN);
    }

    @Override
    public String getID() {
        return "new";
    }

    @Override
    public String getDescription() {
        return "Method body replaced by instructions to return an instance of the class using a constructor with no parameters";
    }
}
