package fr.inria.stamp.mutationtest.descartes.operators;

import fr.inria.stamp.utils.TypeHelper;
import org.pitest.reloc.asm.Opcodes;
import org.pitest.reloc.asm.Type;
import org.pitest.reloc.asm.MethodVisitor;
import org.pitest.reloc.asm.commons.Method;

/**
 * A mutation operator that replaces the method body by a return instruction whose result is the given constant
 */
public class ConstantMutationOperator implements MutationOperator {

    final private Object constant;
    final private  String id;

    /**
     * Builds an instance of the operator given a constant value to be used in the return statement.
     *
     * @param constant The constant value to be used
     */
    public ConstantMutationOperator(String id, Object constant) {
        if(!TypeHelper.isConstantType(constant.getClass()))
            throw new IllegalArgumentException();
        this.constant = constant;
        this.id = id;

        //TODO: When the parser is finished this constructor should only take the literal string value, to ensure that always the literal value matches the object value
    }

    /**
     * Give access to the constant value used by the operator
     *
     * @return The constant value used to build the operator instance
     */
    public Object getConstant() { return constant; }

    /**
     * Returns a value indicating whether the operator can transform the given method.
     * In this case, checks if the return type of the method is the same as the type of the constant value.
     *
     * @param method Method to be tested by the operator
     * @return A boolean value indicating if the return type and the constant value type are the same
     */
    public boolean canMutate(Method method) {
        Type methodType = method.getReturnType();
        int typeSort = methodType.getSort();
        Type constantType = Type.getType(constant.getClass());

        if(typeSort == Type.ARRAY || typeSort == Type.METHOD)
            return false;

        if(typeSort == Type.OBJECT)
            return constantType.equals(methodType);

        return methodType.equals(constantType) ||
                methodType.equals(Type.getType(TypeHelper.unwrap(constant.getClass())));
    }

    public void generateCode(MethodVisitor mv) {
        mv.visitLdcInsn(constant);
        Type type = Type.getType(constant.getClass());
        mv.visitInsn(type.getOpcode(Opcodes.IRETURN));
    }

    public String getID() {
        return id;
    }

    public String getDescription() {
        return "All method body replaced by: return " + id;
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