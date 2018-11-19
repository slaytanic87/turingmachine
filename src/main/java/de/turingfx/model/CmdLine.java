package de.turingfx.model;

import de.turingfx.model.exceptions.CmdException;
import de.turingfx.model.turing.Movement;
import de.turingfx.model.state.State;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Lam, Le (msg systems ag) 2018
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CmdLine {

    private int line;

    private State currentState;
    private String readAlphabet;

    private String writeAlphabet;

    /**
     * Every index number represents a tape.
     */
    private List<Movement> movements = new ArrayList<>();
    private State nextState;

    public Movement getMovement(int tapeIndex) {
        return movements.get(tapeIndex);
    }

    public void determineAndSetMovement(String movementStr) throws CmdException {
        if (movements == null) {
            movements = new ArrayList<>();
        } else {
            movements.clear();
        }
        for (char direction: movementStr.toCharArray()){
            try {
                movements.add(Movement.getMovementByChar(direction));
            } catch (CmdException e) {
                throw new CmdException(e.getLocalizedMessage() + " at line " + line);
            }
        }
    }
}