package eu.stamp_project.mutationtest.descartes.operators;

import eu.stamp_project.utils.TypeHelper;
import org.pitest.reloc.asm.MethodVisitor;
import org.pitest.reloc.asm.Opcodes;
import org.pitest.reloc.asm.Type;
import org.pitest.reloc.asm.commons.Method;

/**
 * A mutation operator that replaces the method body by a return instruction whose result is the given constant
 */
public class ConstantMutationOperator extends MutationOperator {

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
    @Override
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

    /**
     * Generates the code associated with the mutation.
     * @param method Method to which the mutation should be applied
     * @param mv MethodVisitor in charge of code generation.
     */
    @Override
    public void generateCode(Method method, MethodVisitor mv) {
        assert canMutate(method);
        mv.visitLdcInsn(constant);
        Type methodType = method.getReturnType();
        mv.visitLdcInsn(constant);
        mv.visitInsn(methodType.getOpcode(Opcodes.IRETURN));
    }

    @Override
    public String getID() {
        return id;
    }

    @Override
    public String getDescription() {
        return "All method body replaced by: return " + id;
    }

}
