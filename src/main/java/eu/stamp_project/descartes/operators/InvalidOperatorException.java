package eu.stamp_project.descartes.operators;


public class InvalidOperatorException extends RuntimeException {

    public InvalidOperatorException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidOperatorException(String message) {
        super(message);
    }
}
