package fr.inria.stamp.mutationtest.descartes.operators;

import org.objectweb.asm.commons.Method;
import org.pitest.util.Log;
import java.util.logging.Level;

/**
 * Replaces the method body with a <code>return null<code/> statement
 */
public class NullMutantOperator implements MutationOperator{

    /**
     * Returns a value indicating whether the operator can transform the given method.
     * In this case whether <code>null<code/> could be assigned to the return type.
     *
     * @param method Method to be tested by the operator
     * @return A boolean value indicating if null can be assigned to the return type
     */
    public boolean canMutate(Method method) {
        String className = method.getReturnType().getClassName();
        try {
            return !Class.forName(className).isPrimitive();
        }
        catch(ClassNotFoundException exc) {
            org.pitest.util.Log.getLogger().log(Level.WARNING, "Could not find suitable class: " + className);
            return false;
        }
    }

    public void generateCode() {

    }
}
