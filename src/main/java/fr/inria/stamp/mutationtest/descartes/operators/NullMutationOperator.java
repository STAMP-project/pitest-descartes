package fr.inria.stamp.mutationtest.descartes.operators;

import org.pitest.reloc.asm.MethodVisitor;
import org.pitest.reloc.asm.Opcodes;
import org.pitest.reloc.asm.Type;
import org.pitest.reloc.asm.commons.Method;
import java.util.logging.Level;

/**
 * Replaces the method body with a <code>return null<code/> statement
 */
public class NullMutationOperator extends MutationOperator{

    /**
     * Returns a value indicating whether the operator can transform the given method.
     * In this case whether <code>null<code/> could be assigned to the return type.
     *
     * @param method Method to be tested by the operator
     * @return A boolean value indicating if null can be assigned to the return type
     */
    @Override
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

    @Override
    public void generateCode(Method method, MethodVisitor mv) {
        mv.visitInsn(Opcodes.ACONST_NULL);
        assert canMutate(method);
        mv.visitInsn(method.getReturnType().getOpcode(Opcodes.IRETURN));
    }

    @Override
    public String getID() {
        return "null";
    }

    @Override
    public String getDescription(){
        return "All methods instructions replaced by: return null;";
    }

    //Singleton pattern implementation
    private static final NullMutationOperator instance = new NullMutationOperator();

    public static NullMutationOperator getInstance() { return instance; }

    private NullMutationOperator() {}
}
