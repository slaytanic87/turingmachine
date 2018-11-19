package de.turingfx.model.exceptions;

/**
 * @author Lam, Le (msg systems ag) 2018
 */
public class CmdException extends Exception {
    public CmdException() {
    }

    public CmdException(String var1) {
        super(var1);
    }

    public CmdException(String var1, Throwable var2) {
        super(var1, var2);
    }

    public CmdException(Throwable var1) {
        super(var1);
    }
}
