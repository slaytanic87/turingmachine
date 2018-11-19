package de.turingfx.model.exceptions;

/**
 * @author Lam, Le (msg systems ag) 2018
 */
public class TuringMachineException extends Exception {

    public TuringMachineException() {
        super();
    }

    public TuringMachineException(String message) {
        super(message);
    }

    public TuringMachineException(String message, Throwable cause) {
        super(message, cause);
    }

    public TuringMachineException(Throwable cause) {
        super(cause);
    }
}
