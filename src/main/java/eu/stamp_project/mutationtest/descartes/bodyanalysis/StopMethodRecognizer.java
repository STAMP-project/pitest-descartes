package eu.stamp_project.mutationtest.descartes.bodyanalysis;

import org.pitest.reloc.asm.Opcodes;

public class StopMethodRecognizer {


    private State initial, state;

    public StopMethodRecognizer() {
        createStates();
        reset();
    }

    private void createStates() {
        State[] states = new State[6];

        for (int i=0; i < states.length; i++)
            states[i] = new State();

        states[5].setFinal(true);

        states[0].movesWith(Opcodes.ALOAD).to(states[1]);
        states[0].has(Transition.withConstantOnStack()).to(states[2]);
        states[0].movesWith(Opcodes.RETURN).to(states[5]);

        states[1].has(Transition.withXLOAD()).to(states[3]);
        states[1].movesWith(Opcodes.GETFIELD).to(states[2]);

        states[2].has(Transition.withXReturn()).to(states[5]);

        states[3].movesWith(Opcodes.PUTFIELD).to(states[4]);

        states[4].movesWith(Opcodes.RETURN).to(states[5]);

        initial = states[0];
    }

    public boolean isOnFinalState() {
        return state.isFinal();
    }

    public void advance(int opcode) {
        if(opcode < 0) return; //Ignore new frame declaration
        state = state.next(opcode);
    }

    public void doNotRecognize() {
        state = State.SINK;
    }

    public void reset() {
        state = initial;
    }

}
