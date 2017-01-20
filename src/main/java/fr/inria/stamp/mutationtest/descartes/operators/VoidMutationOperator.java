package fr.inria.stamp.mutationtest.descartes.operators;

import org.objectweb.asm.Type;
import org.objectweb.asm.commons.Method;

/**
 * A class whose instances are able to mutate void methods
 */
public class VoidMutationOperator  implements MutationOperator {

    /**
     * Returns a value indicating whether the operator can transform the given method.
     * In this case tells if the given method is void.
     *
     * @param method Method to be tested by the operator
     * @return True if the given method is void, false otherwise
     */
    public boolean canMutate(Method method) {
        return method.getReturnType().equals(Type.VOID_TYPE);
    }

    public void generateCode() {

    }
}
