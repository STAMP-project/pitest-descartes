package eu.stamp_project.mutationtest.descartes.operators;

import org.pitest.classinfo.ClassName;
import org.pitest.reloc.asm.MethodVisitor;
import org.pitest.reloc.asm.Opcodes;
import org.pitest.reloc.asm.Type;
import org.pitest.reloc.asm.commons.Method;

import static eu.stamp_project.utils.Utils.isConstructor;

/**
 * A class whose instances are able to mutate void methods and static initializers
 * (Constructors should call super constructors and so they aren't considered here)
 */
public final class VoidMutationOperator extends MutationOperator {

    /**
     * Returns a value indicating whether the operator can transform the given method.
     * In this case tells if the given method is void.
     *
     * @param method Method to be tested by the operator
     * @return True if the given method is void, false otherwise
     */
    @Override
    public boolean canMutate(ClassName className, Method method) {
        //TODO: Detect methods that contain only calls to logging classes or System.out
        return !isConstructor(method.getName()) && method.getReturnType().equals(Type.VOID_TYPE);
    }

    @Override
    public void generateCode(Method method, MethodVisitor mv) {
        mv.visitInsn(Opcodes.RETURN);
    }

    @Override
    public String getID() {
        return "void";
    }

    @Override
    public String getDescription() {
        return "All method instructions removed";
    }

}
