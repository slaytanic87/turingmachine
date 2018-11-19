package de.turingfx.model.turing;

import de.turingfx.model.exceptions.CmdException;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Lam, Le (msg systems ag) 2018
 */
public enum Movement {
    LEFT("L"),
    RIGHT("R"),
    NEUTRAL("N");

    @Getter
    @Setter
    private String code;

    Movement(String code) {
        this.code = code;
    }


    public static Movement getMovementByChar(char direction) throws CmdException {
        for (Movement mv: Movement.values()) {
            if (mv.getCode().equals(String.valueOf(direction))) {
                return mv;
            }
        }
        throw new CmdException("Unknown head direction: " + direction);
    }
}