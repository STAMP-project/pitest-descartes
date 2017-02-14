package fr.inria.stamp.mutationtest.descartes.operators;

import org.pitest.reloc.asm.MethodVisitor;
import org.pitest.reloc.asm.commons.Method;

/**
 * Mutation operator definition
 */
public interface MutationOperator {

    /**
     * Returns a value indicating whether the operator can transform the given method.
     *
     * @param method Method to be tested by the operator
     * @return A boolean value indicating if the mutation can be performed
     */
    public boolean canMutate(Method method);

    public void generateCode(MethodVisitor mv);

}
