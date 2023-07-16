package eu.stamp_project.descartes.interceptors.stopmethods;

import static org.pitest.bytecode.analysis.InstructionMatchers.isA;

import java.util.Collection;
import org.objectweb.asm.tree.*;
import org.pitest.bytecode.analysis.ClassTree;
import org.pitest.bytecode.analysis.MethodTree;
import org.pitest.sequence.QueryParams;
import org.pitest.sequence.SequenceMatcher;
import org.pitest.sequence.SequenceQuery;

@FunctionalInterface
public interface MethodMatcher {

  // MethodTree does not contain a reference to ClassTree.
  // Some matchers require the class context to classify the method
  boolean matches(ClassTree classTree, MethodTree methodTree);

  static boolean matchesNameDesc(MethodTree methodTree, String name, String desc) {
    MethodNode node = methodTree.rawNode();
    return node.name.equals(name) && node.desc.equals(desc);
  }

  static boolean matchesAccess(ClassTree classTree, int access) {
    return (classTree.rawNode().access & access) != 0;
  }

  static boolean matchesAccess(MethodTree methodTree, int access) {
    return (methodTree.rawNode().access & access) != 0;
  }

  static MethodMatcher matcherForNameAndDesc(String name, String desc) {
    return (classTree, methodTree) -> matchesNameDesc(methodTree, name, desc);
  }

  static MethodMatcher matcherForAccess(int access) {
    return (classTree, methodTree) -> matchesAccess(methodTree, access);
  }

  static MethodMatcher matcherForBodyWithPattern(SequenceQuery<AbstractInsnNode> body) {
    final SequenceMatcher<AbstractInsnNode> matcher =
        body.compile(
            QueryParams.<AbstractInsnNode>params()
                .withIgnores(
                    isA(LabelNode.class).or(isA(FrameNode.class)).or(isA(LineNumberNode.class))));
    return (classTree, methodTree) -> matcher.matches(methodTree.instructions());
  }

  static MethodMatcher matcherForAnyOf(Collection<MethodMatcher> matchers) {
    return (classTree, methodTree) ->
        matchers.stream().anyMatch(matcher -> matcher.matches(classTree, methodTree));
  }
}
