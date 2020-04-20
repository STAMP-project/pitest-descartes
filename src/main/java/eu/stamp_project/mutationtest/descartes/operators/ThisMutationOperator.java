package eu.stamp_project.mutationtest.descartes.operators;

import org.pitest.classinfo.ClassName;
import org.pitest.reloc.asm.MethodVisitor;
import org.pitest.reloc.asm.Opcodes;
import org.pitest.reloc.asm.Type;
import org.pitest.reloc.asm.commons.Method;

/**
 * Replaces the method body with a <code>return null</code> statement
 */
public class ThisMutationOperator extends MutationOperator {

    /**
     * Returns a value indicating whether the operator can transform the given
     * method. In this case whether <code>null</code> could be assigned to the
     * return type.
     *
     * @param method Method to be tested by the operator
     * @return A boolean value indicating if null can be assigned to the return type
     */
    @Override
    public boolean canMutate(ClassName className, Method method) {
        String classNameAsInternal = className.asInternalName();
        String methodReturn = method.getReturnType().getInternalName();
        // if (classNameAsInternal.equals(methodReturn)) {
        //     return true;
        // }
        // return false;
        return classNameAsInternal.equals(methodReturn);
    }

    @Override
    public void generateCode(Method method, MethodVisitor mv) {
        mv.visitInsn(Opcodes.ACONST_NULL);
        mv.visitInsn(Opcodes.ARETURN);
    }

    @Override
    public String getID() {
        return "this";
    }

    @Override
    public String getDescription() {
        return "All method instructions replaced by: return this;";
    }

}
