package eu.stamp_project.mutationtest.descartes.stopmethods;

import org.objectweb.asm.tree.*;
import org.pitest.bytecode.analysis.ClassTree;
import org.pitest.bytecode.analysis.MethodTree;
import org.pitest.sequence.Context;
import org.pitest.sequence.QueryParams;
import org.pitest.sequence.SequenceMatcher;
import org.pitest.sequence.SequenceQuery;

import java.util.Collection;
import java.util.List;

import static org.pitest.bytecode.analysis.InstructionMatchers.isA;

@FunctionalInterface
public interface StopMethodMatcher {

    //MethodTree does not contain a reference to ClassTree.
    //Some matchers require the class class context to classify the method
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

    static StopMethodMatcher forNameDesc(String name, String desc) {
        return (classTree, methodTree) -> matchesNameDesc(methodTree, name, desc);
    }

    static StopMethodMatcher forAccess(int access) {
        return (classTree, methodTree) -> matchesAccess(methodTree, access);
    }

    static StopMethodMatcher forBody(SequenceQuery<AbstractInsnNode> body) {
        final SequenceMatcher<AbstractInsnNode>  matcher =
                body.compile(
                        QueryParams.<AbstractInsnNode>params()
                        .withIgnores(
                                isA(LabelNode.class)
                                        .or(isA(FrameNode.class))
                                        .or(isA(LineNumberNode.class)))
                );
        return (classTree, methodTree) -> {
            List<AbstractInsnNode> instructions = methodTree.instructions();
            Context<AbstractInsnNode> context = Context.start(methodTree.instructions());
            // Ensure that matcher has found a match and that all instructions has been read.
            return matcher.matches(instructions, context) && context.position() == instructions.size() - 1;
        };
    }

    static  StopMethodMatcher any(Collection<StopMethodMatcher> matchers) {
        return (classTree, methodTree) ->
                matchers.stream().anyMatch(matcher -> matcher.matches(classTree, methodTree));
    }
}
