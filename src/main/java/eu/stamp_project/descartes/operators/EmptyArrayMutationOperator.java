package eu.stamp_project.descartes.operators;

import eu.stamp_project.descartes.annotations.Operator;
import eu.stamp_project.descartes.codemanipulation.MethodInfo;
import org.pitest.reloc.asm.Type;
import org.pitest.reloc.asm.commons.GeneratorAdapter;

@Operator(identifier = "empty", description = "Method body replaced by instructions that returns an empty array of the corresponding type")
public class EmptyArrayMutationOperator extends MutationOperator {

    @Override
    public boolean canMutate(MethodInfo method) {
        return method.getReturnType().getSort() == Type.ARRAY;
    }

    @Override
    protected void generateCode(MethodInfo method, GeneratorAdapter generator) {
        Type arrayType = method.getReturnType();
        for (int i = arrayType.getDimensions(); i > 0; i--) {
            generator.push(0);
        }
        //Removing the first [ results in the element type for a one-dimensional array
        // or an array with one dimension less for multi-arrays
        Type lowerDimensionType = Type.getType(arrayType.getDescriptor().substring(1));
        generator.newArray(lowerDimensionType);
        generator.returnValue();
    }
}
