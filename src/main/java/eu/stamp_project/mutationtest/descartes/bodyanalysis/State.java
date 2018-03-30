package eu.stamp_project.mutationtest.descartes.bodyanalysis;

import java.util.LinkedList;

public class State {

    public static State SINK = new State();

    LinkedList<Transition> transitions;

    private boolean isFinal = false;

    public State() {
        transitions = new LinkedList<Transition>();
    }

    public State next(int opcode) {
        for (Transition tr :
                transitions) {
            if (tr.accepts(opcode))
                return tr.getState();
        }
        return SINK;
    }

    public Transition has(Transition tr) {
        transitions.add(tr);
        return tr;
    }

    public Transition movesWith(int opcode) {
        return has(Transition.withOpcode(opcode));
    }

    public void alwaysMovesTo(State other) {
        has(Transition.withAnyOpcode()).to(other);
    }

    public boolean isFinal() {
        return isFinal;
    }

    public void setFinal(boolean isFinal) {
        this.isFinal = isFinal;
    }
}
