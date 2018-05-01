package eu.stamp_project.mutationtest.descartes.stopmethods;


import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode; //These two may have to be relocated
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.Opcodes;
import org.pitest.sequence.*;

import static org.pitest.sequence.QueryStart.match;

import static eu.stamp_project.mutationtest.descartes.stopmethods.StopMethodMatcher.*;

import static org.pitest.bytecode.analysis.InstructionMatchers.opCode;

public interface StopMethodMatchers {

    static StopMethodMatcher isEnumGenerated() {
        return ((classTree, methodTree) -> {
            ClassNode classNode = classTree.rawNode();
            //Inside enum declaration
            if(!classNode.superName.equals("java/lang/Enum"))
                return false;
            //Static
            MethodNode methodNode = methodTree.rawNode();
            if((methodNode.access & Opcodes.ACC_STATIC) != 0)
                return false;
            String returnTypeDescription = "L" + classNode.name + ";";
            // Class valueOf(String) or  Class[] values()
            //TODO: See if it is convenient to leave an overloaded version with MethodNode
            return matchesNameDesc(methodTree,"valueOf", "(Ljava/lang/String;)" + returnTypeDescription)
                    || matchesNameDesc(methodTree, "values", "()[" + returnTypeDescription);
        });
    }

    static  StopMethodMatcher isToString() {
        return forNameDesc("toString", "()Ljava/lang/String");
    }

    static StopMethodMatcher isHashCode() {
        return forNameDesc("hashCode", "()I");
    }

    static StopMethodMatcher isDeprecated() {
        return forAccess(Opcodes.ACC_DEPRECATED);
    }

    static StopMethodMatcher isEmptyVoid() {
        return forBody(QueryStart.match(opCode(Opcodes.RETURN)));
    }

    static StopMethodMatcher isSynthetic() {
        return (classTree, methodTree) -> methodTree.isSynthetic();
    }

    static StopMethodMatcher isSimpleGetter() {
        return forBody(
                (match(opCode(Opcodes.GETSTATIC))
                        .or(match(opCode(Opcodes.ALOAD)).then(opCode(Opcodes.GETFIELD))))
                    .then(new Match<AbstractInsnNode>() {
                        @Override
                        public boolean test(Context<AbstractInsnNode> context, AbstractInsnNode abstractInsnNode) {
                            int opcode =  abstractInsnNode.getOpcode();
                            return opcode >= Opcodes.IRETURN && opcode <= Opcodes.RETURN;
                        }
                    }));
    }




}
