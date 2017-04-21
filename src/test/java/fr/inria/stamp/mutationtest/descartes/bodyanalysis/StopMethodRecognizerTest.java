package fr.inria.stamp.mutationtest.descartes.bodyanalysis;

import fr.inria.stamp.mutationtest.test.Calculator;
import fr.inria.stamp.mutationtest.test.StopMethods;
import fr.inria.stamp.mutationtest.test.TestUtils;
import org.junit.Test;
import org.pitest.reloc.asm.tree.AbstractInsnNode;
import org.pitest.reloc.asm.tree.MethodNode;

import java.util.ListIterator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class StopMethodRecognizerTest {

    @Test
    public void shouldRecognize() {
        for (MethodNode method :
                TestUtils.getMethodNodes(StopMethods.class)) {
            if(!method.name.equals("<init>"))
                assertTrue("Stop method " + method.name + " not recognized", isStopMethod(method));
        }
    }

    @Test
    public void shouldNotRecognize() {
        for (MethodNode method :
                TestUtils.getMethodNodes(Calculator.class)) {
            if(!method.name.equals("<init>"))
                assertFalse("Non-stop method " + method.name + " recognized.", isStopMethod(method));
        }
    }

    private boolean isStopMethod(MethodNode method) {
        StopMethodRecognizer recognizer = new StopMethodRecognizer();
        ListIterator<AbstractInsnNode> iterator = method.instructions.iterator();
        while(iterator.hasNext()) {
            AbstractInsnNode instruction = iterator.next();
            recognizer.advance(instruction.getOpcode());
        }
        return recognizer.isOnFinalState();
    }

}