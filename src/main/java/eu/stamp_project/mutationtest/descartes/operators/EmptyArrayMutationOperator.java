package eu.stamp_project.mutationtest.descartes.operators;

import org.pitest.reloc.asm.MethodVisitor;
import org.pitest.reloc.asm.Opcodes;
import org.pitest.reloc.asm.Type;
import org.pitest.reloc.asm.commons.Method;

public class EmptyArrayMutationOperator extends MutationOperator {

    /**
     * Returns a value indicating whether the operator can transform the given method.
     * In this case, the resulting code will return an empty array of the corresponding type.
     * @param method Method to be tested by the operator
     * @return A boolean value indicating if the mutation can be performed
     */
    public boolean canMutate(Method method) {
        return method.getReturnType().getSort() == Type.ARRAY;
    }

    private final static int[] type2opcode = {0,
            Opcodes.T_BOOLEAN,
            Opcodes.T_CHAR,
            Opcodes.T_BYTE,
            Opcodes.T_SHORT,
            Opcodes.T_INT,
            Opcodes.T_FLOAT,
            Opcodes.T_LONG,
            Opcodes.T_DOUBLE
    };

    public void generateCode(Method method, MethodVisitor mv) {
        Type arrayType = method.getReturnType();
        Type elementType = arrayType.getElementType();
        mv.visitInsn(Opcodes.ICONST_0);

        if (arrayType.getDimensions() > 1) {
            //Multidimensional array
            for(int i=1; i< arrayType.getDimensions(); i++) {
                mv.visitInsn(Opcodes.ICONST_0);
            }
            mv.visitMultiANewArrayInsn(arrayType.getDescriptor(), arrayType.getDimensions());
        }
        else if(elementType.getSort() == Type.OBJECT){
            mv.visitTypeInsn(Opcodes.ANEWARRAY, elementType.getInternalName());
        }
        else {
            mv.visitIntInsn(Opcodes.NEWARRAY, type2opcode[elementType.getSort()]);
        }
        mv.visitInsn(Opcodes.ARETURN);
    }

    public String getID() {
        return "empty";
    }

    public String getDescription() {
        return "Method body replaced by instructions that returns an empty array of the corresponding type";
    }

}
