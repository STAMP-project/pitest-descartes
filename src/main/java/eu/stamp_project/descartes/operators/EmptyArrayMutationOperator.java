package eu.stamp_project.descartes.operators;

import eu.stamp_project.descartes.annotations.Operator;
import eu.stamp_project.descartes.codemanipulation.MethodInfo;
import org.pitest.reloc.asm.MethodVisitor;
import org.pitest.reloc.asm.Opcodes;
import org.pitest.reloc.asm.Type;
import org.pitest.reloc.asm.commons.GeneratorAdapter;
import org.pitest.reloc.asm.commons.InstructionAdapter;

@Operator(identifier = "empty", description = "Method body replaced by instructions that returns an empty array of the corresponding type")
public class EmptyArrayMutationOperator extends MutationOperator {

    public boolean canMutate(MethodInfo method) {
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

    public void generateCode(MethodInfo method, MethodVisitor mv) {
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
}
