package eu.stamp_project.descartes.interceptors.stopmethods;

import static org.pitest.bytecode.analysis.InstructionMatchers.opCode;
import static org.pitest.sequence.Result.result;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.pitest.sequence.Match;
import org.pitest.sequence.Result;

public final class InstructionMatchers {

  private InstructionMatchers() {}

  private static Match<AbstractInsnNode> opCodeBetween(int lower, int upper) {
    return (c, abstractInsnNode) -> {
      int opcode = abstractInsnNode.getOpcode();
      return result(opcode >= lower && opcode <= upper, c);
    };
  }

  private static Match<AbstractInsnNode> isLoadWithVar(int var) {
    return (context, instruction) -> {
      if (!(instruction instanceof VarInsnNode)) {
        return result(false, context);
      }
      VarInsnNode node = (VarInsnNode) instruction;
      return result(node.getOpcode() == Opcodes.ALOAD && node.var == var, context);
    };
  }


  public static final Match<AbstractInsnNode> RETURN = opCode(Opcodes.RETURN);
  public static final Match<AbstractInsnNode> ARETURN = opCode(Opcodes.ARETURN);
  public static final Match<AbstractInsnNode> ALOAD = opCode(Opcodes.ALOAD);
  public static final Match<AbstractInsnNode> ILOAD = opCode(Opcodes.ILOAD);
  public static final Match<AbstractInsnNode> ICONST_0 = opCode(Opcodes.ICONST_0);
  public static final Match<AbstractInsnNode> GETSTATIC = opCode(Opcodes.GETSTATIC);
  public static final Match<AbstractInsnNode> PUTSTATIC = opCode(Opcodes.PUTSTATIC);
  public static final Match<AbstractInsnNode> GETFIELD = opCode(Opcodes.GETFIELD);
  public static final Match<AbstractInsnNode> PUTFIELD = opCode(Opcodes.PUTFIELD);
  public static final Match<AbstractInsnNode> NEWARRAY = opCode(Opcodes.NEWARRAY);
  public static final Match<AbstractInsnNode> ANEWARRAY = opCode(Opcodes.ANEWARRAY);
  public static final Match<AbstractInsnNode> ACONST_NULL = opCode(Opcodes.ACONST_NULL);
  public static final Match<AbstractInsnNode> LDC = opCode(Opcodes.LDC);
  public static final Match<AbstractInsnNode> INVOKESTATIC = opCode(Opcodes.INVOKESTATIC);

  public static final Match<AbstractInsnNode> XRETURN
      = opCodeBetween(Opcodes.IRETURN, Opcodes.RETURN);

  public static final Match<AbstractInsnNode> XLOAD
      = opCodeBetween(Opcodes.ILOAD, Opcodes.ALOAD);

  public static final Match<AbstractInsnNode> XLOAD_N
      = opCodeBetween(21, 45);
  public static final Match<AbstractInsnNode> ANY_CONSTANT_PUSH
      = opCodeBetween(Opcodes.ACONST_NULL, 20);

  public static final Match<AbstractInsnNode> LOAD_THIS = isLoadWithVar(0);
  public static final Match<AbstractInsnNode> LOAD_FIRST_PARAMETER = isLoadWithVar(1);

  public static final Match<AbstractInsnNode> LOAD_ANY_PARAMETER
      = (context, instruction) ->
      Result.result(
          instruction instanceof VarInsnNode && ((VarInsnNode) instruction).var > 0,
          context
      );

  public static final Match<AbstractInsnNode> ANY_INVOKE_WITH_RECEIVER
      = (context, instructionNode) -> {
        int opcode = instructionNode.getOpcode();
        return Result.result(
            opcode == Opcodes.INVOKEVIRTUAL
                || opcode == Opcodes.INVOKESPECIAL
                || opcode == Opcodes.INVOKEINTERFACE,
            context
        );
      };

  public static final Match<AbstractInsnNode> KOTLIN_CHECK_PARAMETER_IS_NOT_NULL =
      (context, instruction) -> {
        if (!(instruction instanceof MethodInsnNode)) {
          return result(false, context);
        }
        MethodInsnNode node = (MethodInsnNode) instruction;
        return result(
            node.getOpcode() == Opcodes.INVOKESTATIC
                && "checkParameterIsNotNull".equals(node.name)
                && "kotlin/jvm/internal/Intrinsics".equals(node.owner)
                && "(Ljava/lang/Object;Ljava/lang/String;)V".equals(node.desc),
            context);
      };

}

