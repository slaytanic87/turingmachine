package de.turingfx.model.state;

import lombok.Getter;

/**
 * @author Lam, Le (msg systems ag) 2018
 */
public enum StateStatus {

    NEXT_STATE_AVAILABLE("OK"),
    END_STATE_REACHED("ACCEPTED"),
    DECLINED("DECLINED"),
    INTERNAL_ERROR("INTERNAL ERROR");

    @Getter
    private String label;

    StateStatus(String label) {
        this.label = label;
    }
}