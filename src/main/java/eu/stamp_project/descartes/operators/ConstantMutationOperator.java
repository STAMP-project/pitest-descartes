package eu.stamp_project.descartes.operators;

import eu.stamp_project.descartes.codemanipulation.MethodInfo;
import eu.stamp_project.utils.TypeHelper;
import org.pitest.reloc.asm.MethodVisitor;
import org.pitest.reloc.asm.Opcodes;
import org.pitest.reloc.asm.Type;

/**
 * A mutation operator that replaces the method body by a return instruction whose result is the given constant
 */
public class ConstantMutationOperator extends MutationOperator {

    private final Object constant;
    private final String id;

    /**
     * Builds an instance of the operator given a constant value to be used in the return statement.
     *
     * @param id The identifier for the instance of the operator. It does not have to coincide with the constant value.
     * @param constant The constant value to be used
     */
    public ConstantMutationOperator(String id, Object constant) {
        // We actually need the identifier to be able to distinguish the operator.
        // Besides, there could be a scenario in which two different operators can produce the same constant.
        if(!TypeHelper.isConstantType(constant.getClass()))
            throw new IllegalArgumentException();
        this.constant = constant;
        this.id = id;

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
    public boolean canMutate(MethodInfo method) {
        Type methodType = method.getReturnType();
        int typeSort = methodType.getSort();
        Type constantType = Type.getType(constant.getClass());

        if(typeSort == Type.ARRAY || typeSort == Type.METHOD)
            return false;

        if(typeSort == Type.OBJECT)
            return constantType.equals(methodType);

        return methodType.equals(constantType) ||
                methodType.equals(wrapperType());
    }

    /**
     * Generates the code associated with the mutation.
     * @param method Method to which the mutation should be applied
     * @param mv MethodVisitor in charge of code generation.
     */
    @Override
    public void generateCode(MethodInfo method, MethodVisitor mv) {
        assert canMutate(method);
        mv.visitLdcInsn(constant);

        Type methodType = method.getReturnType();
        Type constantType = Type.getType(constant.getClass());
        if(constant.getClass() != String.class && methodType.equals(constantType)) {
            //Wrapper type, as the constant will always have the wrapper type

            String target =  methodType.getInternalName();
            String descriptor = String.format("(%s)L%s;", wrapperType().getInternalName(), target);
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, target, "valueOf", descriptor, false);
            mv.visitInsn(Opcodes.ARETURN);
        }
        else {
            //Primitive type or String
            mv.visitInsn(methodType.getOpcode(Opcodes.IRETURN));
        }
    }

    private Type wrapperType() {
        return Type.getType(TypeHelper.unwrap(constant.getClass()));
    }

    @Override
    public String getIdentifier() {
        return id;
    }

    @Override
    public String getDescription() {
        return "All method instructions replaced by: return " + id;
    }

}
