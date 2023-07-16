package eu.stamp_project.descartes.interceptors.stopmethods;

import static eu.stamp_project.descartes.interceptors.stopmethods.InstructionMatchers.*;
import static org.pitest.sequence.QueryStart.match;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public final class StopMethodMatchers {

  private StopMethodMatchers() {}

  public static final MethodMatcher IS_STATIC_INITIALIZER
      = MethodMatcher.matcherForNameAndDesc("<clinit>", "()V");

  public static final MethodMatcher IS_HASH_CODE
      = MethodMatcher.matcherForNameAndDesc("hashCode", "()I");

  public static final MethodMatcher IS_TO_STRING
      = MethodMatcher.matcherForNameAndDesc("toString", "()Ljava/lang/String;");

  public static final MethodMatcher IS_DEPRECATED
      = (classTree, methodTree) ->
        MethodMatcher.matchesAccess(classTree, Opcodes.ACC_DEPRECATED)
            || MethodMatcher.matchesAccess(methodTree, Opcodes.ACC_DEPRECATED)
      ;

  public static final MethodMatcher IS_SYNTEHTIC
      = (classTree, methodTree) ->
        methodTree.isSynthetic() && !methodTree.rawNode().name.startsWith("lambda$");

  public static final MethodMatcher IS_EMPTY_VOID
      = MethodMatcher.matcherForBodyWithPattern(match(RETURN));

  public static final MethodMatcher RETURNS_NULL
      = MethodMatcher.matcherForBodyWithPattern(match(ACONST_NULL).then(ARETURN));

  public static final MethodMatcher RETURNS_THIS
      = MethodMatcher.matcherForBodyWithPattern(match(LOAD_THIS).then(ARETURN));

  // ALOAD_X (DRETURN | FRETURN | IRETURN | LRETURN | ARETURN)
  public static final MethodMatcher RETURNS_PARAMETER
      = MethodMatcher.matcherForBodyWithPattern(match(LOAD_ANY_PARAMETER).then(XRETURN));

  public static final MethodMatcher RETURNS_CONSTANT
      = MethodMatcher.matcherForBodyWithPattern(match(ANY_CONSTANT_PUSH).then(XRETURN));


  public static final MethodMatcher IS_SIMPLE_GETTER
      = MethodMatcher.matcherForBodyWithPattern(
        (match(GETSTATIC).or(match(ALOAD).then(GETFIELD))).then(XRETURN));

  public static final MethodMatcher RETURNS_EMPTY_ARRAY
      = MethodMatcher.matcherForBodyWithPattern(
        match(ICONST_0).then(NEWARRAY.or(ANEWARRAY)).then(ARETURN));

  // ( (ALOAD [ILOAD..ALOAD] PUTFIELD) | (ILOAD PUTSTATIC) ) (RETURN | ALOAD ARETURN)
  public static final MethodMatcher IS_SIMPLE_SETTER
      = MethodMatcher.matcherForBodyWithPattern(
        ((match(ALOAD).then(XLOAD).then(PUTFIELD))
                .or(match(ILOAD).then(PUTSTATIC)))
            .then(match(RETURN).or(match(ALOAD).then(ARETURN))));

  public static final MethodMatcher IS_KOTLIN_GENERATED_SETTER
      = MethodMatcher.matcherForBodyWithPattern(
          match(LOAD_FIRST_PARAMETER)
            .then(LDC)
            .then(KOTLIN_CHECK_PARAMETER_IS_NOT_NULL)
            .then(LOAD_THIS)
            .then(LOAD_FIRST_PARAMETER)
            .then(PUTFIELD)
            .then(RETURN));

  public static final MethodMatcher IS_DELEGATE
      = MethodMatcher.matcherForBodyWithPattern((match(ALOAD.or(GETSTATIC))
                .or(match(ALOAD).then(GETFIELD)) // Target on the stack
                .zeroOrMore(match(XLOAD_N)) // Param loop
                .then(ANY_INVOKE_WITH_RECEIVER)
                .then(XRETURN))
            .or(match(XLOAD_N).zeroOrMore(match(XLOAD_N)).then(INVOKESTATIC).then(XRETURN))
            .or(
                match(INVOKESTATIC)
                    .then(XRETURN)) // Not expressive enough to match zeroOrMore form the beginning
        );

  public static final MethodMatcher IS_ENUM_GENERATED
      = (classTree, methodTree) -> {
        ClassNode classNode = classTree.rawNode();
        // Inside enum declaration
        if (!"java/lang/Enum".equals(classNode.superName)) {
          return false;
        }
        // Static
        MethodNode methodNode = methodTree.rawNode();
        if ((methodNode.access & Opcodes.ACC_STATIC) == 0) {
          // Both methods are static, if it is not a static method, then false
          return false;
        }
        String returnTypeDescription = "L" + classNode.name + ";";
        // Class valueOf(String) or  Class[] values()
        return MethodMatcher.matchesNameDesc(
            methodTree, "valueOf", "(Ljava/lang/String;)" + returnTypeDescription)
            || MethodMatcher.matchesNameDesc(methodTree, "values", "()[" + returnTypeDescription)
            // Compatibility with other JDKs
            || MethodMatcher.matchesNameDesc(
            methodTree, "$values", "()[" + returnTypeDescription);
      };
}
