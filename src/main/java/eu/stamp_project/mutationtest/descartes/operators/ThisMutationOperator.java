package eu.stamp_project.mutationtest.descartes.operators;

import org.pitest.classinfo.ClassName;
import org.pitest.reloc.asm.MethodVisitor;
import org.pitest.reloc.asm.Opcodes;
import org.pitest.reloc.asm.Type;
import org.pitest.reloc.asm.commons.Method;

/**
 * Replaces the method body with a <code>return this</code> statement
 */
public class ThisMutationOperator extends MutationOperator{

    /**
     * Returns a value indicating whether the operator can transform the given method.
     * In this case whether <code>this</code> could be assigned to the return type.
     *
     * @param method Method to be tested by the operator
     * @return A boolean value indicating if this can be assigned to the return type
     */

    // @Override
    // public boolean canMutate(Method method) {
    //     return true;
    // }

    @Override
    public boolean canMutate(ClassName className, Method method) {
        String[] objArr = method.getReturnType().toString().split("/");
        String[] classArr = className.asJavaName().split("\\.");
        int check = objArr[objArr.length - 1].indexOf(classArr[classArr.length - 1]);
        
        if(check != -1){
            return  true;
        }
        return false;
    }

    @Override
    public void generateCode(Method method, MethodVisitor mv) {
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitInsn(Opcodes.ARETURN);
    }

    @Override
    public String getID() {
        return "this";
    }

    @Override
    public String getDescription(){
        return "All method instructions replaced by: return this;";
    }

}
