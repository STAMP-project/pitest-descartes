package eu.stamp_project.mutationtest.descartes.operators;

import eu.stamp_project.mutationtest.descartes.operators.parsing.OperatorParser;
import org.pitest.reloc.asm.MethodVisitor;
import org.pitest.reloc.asm.commons.Method;

/**
 * Mutation operator definition
 */
public abstract class MutationOperator {

    /**
     * Returns a value indicating whether the operator can transform the given method.
     *
     * @param method Method to be tested by the operator
     * @return A boolean value indicating if the mutation can be performed
     */
    public abstract boolean canMutate(Method method);

    public abstract void generateCode(Method method, MethodVisitor mv);

    public abstract String getID();

    public abstract String getDescription();

    public static MutationOperator fromID(String id) {
        OperatorParser parser = new OperatorParser(id);
        Object value = parser.parse();
        if(parser.hasErrors())
            throw new WrongOperatorException("Invalid operator id: " + parser.getErrors().get(0));
        if(value == null)
            return NullMutationOperator.getInstance();
        if(value.equals(Void.class)) {
            return VoidMutationOperator.getInstance();
        }
        if(value.equals(EmptyArrayMutationOperator.getInstance().getID())) {
            return EmptyArrayMutationOperator.getInstance();
        }
        try {
            return new ConstantMutationOperator(id, value);
        }catch (IllegalArgumentException exc) {
            throw new WrongOperatorException("Invalid operator id", exc);
        }
    }

}
