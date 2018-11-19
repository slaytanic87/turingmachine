package de.turingfx.model.state;

import de.turingfx.model.turing.Movement;
import lombok.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Lam, Le (msg systems ag) 2018
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class State {

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private StateType stateType = StateType.NORMAL;

    private Map<String, String> transitionsState = new HashMap<>();
    private Map<String, List<Movement>> transitionsActions = new HashMap<>();
    private Map<String, String> transitionsWrite = new HashMap<>();

    public String getNext(String inputAlphabet) {
       return transitionsState.get(inputAlphabet);
    }

    public void addNext(String input, String state) {
        transitionsState.put(input, state);
    }

    public List<Movement> getNextMovements(String inputAlphabet) {
        return transitionsActions.get(inputAlphabet);
    }

    public void addNextMovements(String input, List<Movement> movements) {
        transitionsActions.put(input, movements);
    }

    public String getNextWriteActions(String input) {
        return transitionsWrite.get(input);
    }

    public void addNextWriteActions(String input, String writeActions) {
        transitionsWrite.put(input, writeActions);
    }

    public String toString() {
        return "name: " + name + ", state type: " + stateType.toString();
    }
}