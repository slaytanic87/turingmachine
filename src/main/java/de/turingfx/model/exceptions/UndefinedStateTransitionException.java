package de.turingfx.model.exceptions;

/**
 * @author Lam, Le (msg systems ag) 2018
 */
public class UndefinedStateTransitionException extends Exception {

    public UndefinedStateTransitionException() {
        super();
    }

    public UndefinedStateTransitionException(String input) {
        super("Unknown state for: " + input);
    }

    public UndefinedStateTransitionException(String message, Throwable cause) {
        super(message, cause);
    }

    public UndefinedStateTransitionException(Throwable cause) {
        super(cause);
    }
}