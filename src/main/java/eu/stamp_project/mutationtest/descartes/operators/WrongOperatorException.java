package eu.stamp_project.mutationtest.descartes.operators;


public class WrongOperatorException extends RuntimeException {

    public WrongOperatorException(String message, Throwable cause) {
        super(message, cause);
    }

    public WrongOperatorException(String message) {
        super(message);
    }
}
