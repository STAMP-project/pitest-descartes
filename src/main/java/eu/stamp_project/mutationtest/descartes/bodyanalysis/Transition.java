package eu.stamp_project.mutationtest.descartes.bodyanalysis;

import org.pitest.reloc.asm.Opcodes;
import org.pitest.functional.predicate.Predicate;

public abstract class Transition {

    private State state;
    public State getState() {
        return state;
    }

    public void to(State state) {
        this.state = state;

    }

    public abstract boolean accepts(int opcode);

    public static Transition withAnyOpcode() {
        return new Transition() {
            @Override
            public boolean accepts(int opcode) {
                return true;
            }
        };
    }

    public static  Transition withOpcode(final int requiredOpcode) {
        return new Transition() {
            @Override
            public boolean accepts(int opcode) {
                return requiredOpcode == opcode;
            }
        };
    }

    public static Transition withConstantOnStack() {
        return new Transition() {
            @Override
            public boolean accepts(int opcode) {
                return opcode > 0 && opcode <= 20;
            }
        };
    }

    public static Transition withXLOAD() {
        return new Transition() {
            @Override
            public boolean accepts(int opcode) {
                return opcode >= Opcodes.ILOAD && opcode <= Opcodes.ALOAD;
            }
        };
    }

    public static Transition withXReturn() {
        return new Transition() {
            @Override
            public boolean accepts(int opcode) {
                return opcode >= Opcodes.IRETURN && opcode <= Opcodes.RETURN;
            }
        };
    }

    public static Transition with(final Predicate<Integer> pred){
        return new Transition() {
            @Override
            public boolean accepts(int opcode) {
                return pred.apply(opcode);
            }
        };
    }
}
