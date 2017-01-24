package fr.inria.stamp.mutationtest.descartes.operators;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.Method;

/**
 * A mutation operator that replaces the method body by a return instruction whose result is the given constant
 */
public class ConstantMutationOperator<T> implements MutationOperator {

    final private T constant;

    /**
     * Builds an instance of the operator given a constant value to be used in the return statement.
     *
     * @param constant The constant value to be used
     */
    public ConstantMutationOperator(T constant) {
        this.constant = constant;

    }

    /**
     * Give access to the constant value used by the operator
     *
     * @return The constant value used to build the operator instance
     */
    public T getConstant() { return constant; }

    /**
     * Returns a value indicating whether the operator can transform the given method.
     * In this case, checks if the return type of the method is the same as the type of the constant value.
     *
     * @param method Method to be tested by the operator
     * @return A boolean value indicating if the return type and the constant value type are the same
     */
    public boolean canMutate(Method method) {

        return method.getReturnType().getInternalName().equals(constant.getClass().getName());
    }

    public void generateCode(MethodVisitor mv) {

    }
}

//TODO: Deal with primitives and wrappers. There are important differences on their handling when code is being generated.

/*
 public static int get3();
    Code:
       0: iconst_3
       1: ireturn

  public static java.lang.Integer getThree();
    Code:
       0: iconst_3
       1: invokestatic  #5                  // Method java/lang/Integer.valueOf:(I)Ljava/lang/Integer;
       4: areturn

 */
//TODO: Check also Type.getType(Class<?>) in order to implement type comparison. Sadly there is no method that works the other way around.