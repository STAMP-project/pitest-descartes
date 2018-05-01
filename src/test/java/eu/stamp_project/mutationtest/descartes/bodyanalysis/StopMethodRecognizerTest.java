package eu.stamp_project.mutationtest.descartes.bodyanalysis;

import eu.stamp_project.mutationtest.test.Calculator;
import eu.stamp_project.mutationtest.test.StopMethods;
import eu.stamp_project.mutationtest.test.TestUtils;

import org.junit.Ignore;
import org.junit.Test;
import org.pitest.reloc.asm.tree.AbstractInsnNode;
import org.pitest.reloc.asm.tree.MethodNode;

import java.util.ListIterator;

import static eu.stamp_project.utils.Utils.isConstructor;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


@Ignore
public class StopMethodRecognizerTest {

    @Test
    public void shouldRecognize() {
        for (MethodNode method :
                TestUtils.getMethodNodes(StopMethods.class)) {
            if(!isConstructor(method.name))
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