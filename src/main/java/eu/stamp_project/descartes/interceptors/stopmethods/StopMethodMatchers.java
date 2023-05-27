package eu.stamp_project.descartes.interceptors.stopmethods;

import org.objectweb.asm.tree.*;
import org.pitest.sequence.Match;
import org.pitest.sequence.SequenceQuery;

import static eu.stamp_project.descartes.interceptors.stopmethods.StopMethodMatcher.*;
import static org.objectweb.asm.Opcodes.*;
import static org.pitest.bytecode.analysis.InstructionMatchers.opCode;
import static org.pitest.sequence.QueryStart.match;
import static org.pitest.sequence.Result.result;

public class StopMethodMatchers {

    private StopMethodMatchers() { }

    static StopMethodMatcher isEnumGenerated() {
        return ((classTree, methodTree) -> {
            ClassNode classNode = classTree.rawNode();
            //Inside enum declaration
            if(!classNode.superName.equals("java/lang/Enum"))
                return false;
            //Static
            MethodNode methodNode = methodTree.rawNode();
            if((methodNode.access & ACC_STATIC) == 0) //Both methods are static, if it is not a static method, then false
                return false;
            String returnTypeDescription = "L" + classNode.name + ";";
            // Class valueOf(String) or  Class[] values()
            return matchesNameDesc(methodTree,"valueOf", "(Ljava/lang/String;)" + returnTypeDescription)
                    || matchesNameDesc(methodTree, "values", "()[" + returnTypeDescription)
                    // Compatibility with other JDKs
                    || matchesNameDesc(methodTree, "$values", "()[" + returnTypeDescription);
        });
    }

    static  StopMethodMatcher isToString() {
        return forNameDesc("toString", "()Ljava/lang/String;");
    }

    static StopMethodMatcher isHashCode() {
        return forNameDesc("hashCode", "()I");
    }

    static StopMethodMatcher isDeprecated() {
        return (classTree, methodTree) ->
                matchesAccess(classTree, ACC_DEPRECATED) || matchesAccess(methodTree, ACC_DEPRECATED);
    }

    static StopMethodMatcher isEmptyVoid() {
        return forBody(match(opCode(RETURN)));
    }

    static StopMethodMatcher isSynthetic() {
        return (classTree, methodTree) -> methodTree.isSynthetic() && !methodTree.rawNode().name.startsWith("lambda$");
    }

    static Match<AbstractInsnNode> opCodeBetween(int lower, int upper) {
        return (c, abstractInsnNode) -> {
            int opcode = abstractInsnNode.getOpcode();
            return result(opcode >= lower && opcode <= upper, c);
        };
    }

    static StopMethodMatcher isSimpleGetter() {
        return forBody(
                (match(opCode(GETSTATIC))
                        .or(match(opCode(ALOAD)).then(opCode(GETFIELD))))
                    .then(opCodeBetween(IRETURN, RETURN))
        );
    }

    static StopMethodMatcher isSimpleSetter() {
        // ( (ALOAD [ILOAD..ALOAD] PUTFIELD) | (ILOAD PUTSTATIC) ) (RETURN | ALOAD ARETURN)
        return forBody(
                (
                        (
                                match(opCode(ALOAD)).then(opCodeBetween(ILOAD, ALOAD)).then(opCode(PUTFIELD))
                        ).or(match(opCode(ILOAD)).then(opCode(PUTSTATIC)))
                ).then(
                        match(opCode(RETURN)).or(match(opCode(ALOAD)).then(opCode(ARETURN))))
        );

    }

    static StopMethodMatcher returnsAConstant() {
        return forBody(match(opCodeBetween(1, 20)).then(opCodeBetween(IRETURN, RETURN)));
    }

    static StopMethodMatcher isDelegate() {

        SequenceQuery<AbstractInsnNode> paramMatch = match(opCodeBetween(21, 45));
        Match<AbstractInsnNode> returnOpcode = opCodeBetween(IRETURN, RETURN);
        Match<AbstractInsnNode> anyInvoke = (context, instructionNode) -> {
            int opcode = instructionNode.getOpcode();
            return result(opcode == INVOKEVIRTUAL || opcode == INVOKESPECIAL || opcode == INVOKEINTERFACE, context);
        };

        return forBody((
                        match(opCode(ALOAD).or(opCode(GETSTATIC)))
                        .or(match(opCode(ALOAD)).then(opCode(GETFIELD))) //Target on the stack
                        .zeroOrMore(paramMatch) // Param loop
                        .then(anyInvoke)
                        .then(returnOpcode))
                .or(paramMatch.zeroOrMore(paramMatch).then(opCode(INVOKESTATIC)).then(returnOpcode))
                .or(match(opCode(INVOKESTATIC)).then(returnOpcode)) //Not expressive enough to match zeroOrMore form the beginning
        );
    }

    static StopMethodMatcher isStaticInitializer() {
        return forNameDesc("<clinit>", "()V");
    }

    static StopMethodMatcher returnsAnEmptyArray() {
        return forBody(
                match(opCode(ICONST_0))
                        .then(opCode(NEWARRAY).or(opCode(ANEWARRAY)))
                        .then(opCode(ARETURN))
        );
    }

    static StopMethodMatcher returnsNull() {
        return forBody(
          match(opCode(ACONST_NULL))
          .then(opCode(ARETURN)));
    }

    static StopMethodMatcher returnsThis() {
        Match<AbstractInsnNode> ALOAD_0 = (context, instruction) ->
                result((instruction instanceof VarInsnNode) && ((VarInsnNode) instruction).var == 0, context)
        ;
        return forBody(match(ALOAD_0).then(opCode(ARETURN)));
    }

    static StopMethodMatcher returnsAParameter() {
        // ALOAD_X (DRETURN | FRETURN | IRETURN | LRETURN | ARETURN)
        Match<AbstractInsnNode> ALOAD_X = (context, instruction) ->
                result(instruction instanceof VarInsnNode && ((VarInsnNode) instruction).var > 0, context)
        ;
        return forBody(match(ALOAD_X).then(opCodeBetween(IRETURN, ARETURN)));
    }

    static Match<AbstractInsnNode> aload(int var) {
        return (context, instruction) -> {
            if(!(instruction instanceof VarInsnNode)) {
                return result(false, context);
            }
            VarInsnNode node = (VarInsnNode) instruction;
            return result(node.getOpcode() == ALOAD && node.var == var, context);
        };
    }

    static StopMethodMatcher isKotlinGeneratedSetter() {
        Match<AbstractInsnNode> ALOAD_1 = aload(1);
        Match<AbstractInsnNode> INVOKE_CHECK = (context, instruction) -> {
            if(!(instruction instanceof MethodInsnNode)) {
                return result(false, context);
            }
            MethodInsnNode node = (MethodInsnNode) instruction;
            return result(
                    node.getOpcode() == INVOKESTATIC &&
                    node.name.equals("checkParameterIsNotNull") &&
                    node.owner.equals("kotlin/jvm/internal/Intrinsics") &&
                    node.desc.equals("(Ljava/lang/Object;Ljava/lang/String;)V"),
                    context
            );
        };

        return forBody(
                match(ALOAD_1)
                        .then(opCode(LDC))
                        .then(INVOKE_CHECK)
                        .then(aload(0))
                        .then(ALOAD_1)
                        .then(opCode(PUTFIELD))
                        .then(opCode(RETURN))
        );
    }

}
