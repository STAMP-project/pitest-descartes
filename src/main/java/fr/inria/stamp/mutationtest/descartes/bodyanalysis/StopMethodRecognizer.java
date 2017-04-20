package fr.inria.stamp.mutationtest.descartes.bodyanalysis;


import com.sun.org.apache.bcel.internal.generic.GETFIELD;
import org.pitest.reloc.asm.Opcodes;

public class StopMethodRecognizer {


    private State initial, state;

    public StopMethodRecognizer() {
        createStates();
        reset();
    }

    private void createStates() {
        State[] states = new State[11];

        for (int i=0; i < states.length; i++)
            states[i] = new State();

        states[10].setFinal(true);

        states[0].movesWith(Opcodes.RETURN).to(states[10]);
        states[0].alwaysMovesTo(states[1]);

        states[1].alwaysMovesTo(states[2]);

        states[2].movesWith(Opcodes.ALOAD).to(states[3]);

        states[2].has(Transition.withConstantOnStack()).to(states[8]);

        states[3].has(Transition.withXLOAD()).to(states[4]);
        states[3].movesWith(Opcodes.GETFIELD).to(states[8]);

        states[4].movesWith(Opcodes.PUTFIELD).to(states[5]);

        states[5].alwaysMovesTo(states[6]);

        states[6].alwaysMovesTo(states[7]);

        states[7].movesWith(Opcodes.RETURN).to(states[9]);

        states[8].has(Transition.withXReturn()).to(states[9]);

        states[9].alwaysMovesTo(states[10]);

        initial = states[0];
    }

    public boolean isOnFinalState() {
        return state.isFinal();
    }

    public void advance(int opcode) {
        state = state.next(opcode);
    }

    public void dontRecognize() {
        state = State.SINK;
    }

    public void reset() {
        state = initial;
    }

}
