package eu.stamp_project.mutationtest.descartes.operators;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import org.pitest.reloc.asm.MethodVisitor;
import org.pitest.reloc.asm.Opcodes;
import org.pitest.reloc.asm.Type;
import org.pitest.reloc.asm.commons.Method;

/**
 * Replaces the method body with a return statement, which generates an instance of the specified return type.
 * <br/>
 * This is currently working for: TODO
 */
public class NewInstanceMutationOperator extends MutationOperator {

    /**
     * Returns a value indicating whether the operator can transform the given method.
     */
    @Override
    public boolean canMutate(Method method) {
    	// TODO: check if this is reliable enough for most cases or if this method should already try to create an instance
    	// TODO: manually create instances for well-known interfaces (Collection, List, Set, Map)
    	
        String className = method.getReturnType().getClassName();
        
        try {
        Class<?> returnClass = Class.forName(className);
        
          if (Modifier.isAbstract(returnClass.getModifiers())) {
            return false;
          }
          
          for (Constructor<?> publicConstructor : returnClass.getConstructors()) {
            if (publicConstructor.getParameters().length == 0) {
              return true;
            }
          }
          
          return false;
        }
        catch (ClassNotFoundException e) {
          return false;
        }
    }

    @Override
    public void generateCode(Method method, MethodVisitor mv) {
        assert canMutate(method);
        Type returnType = method.getReturnType();
        mv.visitTypeInsn(Opcodes.NEW, returnType.getInternalName());
        mv.visitInsn(Opcodes.DUP);
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, returnType.getInternalName(), "<init>", "()V", false);
        mv.visitInsn(Opcodes.ARETURN);
    }

    @Override
    public String getID() {
        return "new";
    }

    @Override
    public String getDescription(){
        return "All methods instructions replaced by an instance of the class";
    }
}
