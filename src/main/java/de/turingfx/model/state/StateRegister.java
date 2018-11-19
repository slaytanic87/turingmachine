package de.turingfx.model.state;

import de.turingfx.controller.CommandParser;
import de.turingfx.model.exceptions.CmdException;
import de.turingfx.model.CmdLine;
import de.turingfx.model.exceptions.UndefinedStateTransitionException;
import de.turingfx.model.turing.Movement;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Mealy based state machine.
 * @author Lam, Le (msg systems ag) 2018
 */
public class StateRegister {

    private State currentState;

    private Map<String, State> states = new HashMap<>();

    public State getCurrentState() {
        return currentState;
    }

    public void setCurrentState(State state) {
        this.currentState = state;
    }

    public State getNextState(String input) throws UndefinedStateTransitionException {
        State state = states.get(currentState.getName());
        if (state == null) {
            throw new UndefinedStateTransitionException(input);
        }
        String nextStateName = state.getNext(input);
        if (StringUtils.isBlank(nextStateName)) {
            throw new UndefinedStateTransitionException(input);
        }
        return states.get(nextStateName);
    }

    public List<Movement> getNextMovements(String input) {
        return currentState.getNextMovements(input);
    }

    public String getNextWriteAlphabets(String input) {
        return currentState.getNextWriteActions(input);
    }

    public State getStateByName(String name) {
        return states.get(name);
    }

    public void determineStates(List<CmdLine> commands) throws CmdException {
        for (CmdLine cmd: commands) {
            State currentState = cmd.getCurrentState();
            String readAlphabet = cmd.getReadAlphabet();

            String writeAlphabet = cmd.getWriteAlphabet();
            State nextState = cmd.getNextState();
            List<Movement> nextMovements = cmd.getMovements();

            if (nextState.getStateType().equals(StateType.END)) {
                states.put(nextState.getName().toUpperCase(), nextState);
            }

            if (states.get(currentState.getName()) != null) {
                states.get(currentState.getName()).addNext(readAlphabet, nextState.getName());
                states.get(currentState.getName()).addNextMovements(readAlphabet, nextMovements);
                states.get(currentState.getName()).addNextWriteActions(readAlphabet, writeAlphabet);
            } else {
                currentState.addNext(readAlphabet, nextState.getName());
                currentState.addNextMovements(readAlphabet, nextMovements);
                currentState.addNextWriteActions(readAlphabet, writeAlphabet);
                states.put(currentState.getName(), currentState);
            }
        }
        checkForStartAndEndState();
        currentState = states.get(CommandParser.START_STATE_NAME);
    }

    private void checkForStartAndEndState() throws CmdException {
        boolean hasStartState = states.containsKey(CommandParser.START_STATE_NAME);
        boolean hasEndState = states.containsKey(CommandParser.END_STATE_NAME);
        if (!hasStartState || !hasEndState) {
            throw new CmdException("No start or end state was found: start = "
                    + hasStartState + " end = " + hasEndState);
        }
    }

    public List<State> getStates() {
        return new ArrayList<>(states.values());
    }

    public void reset() {
        this.states.clear();
        currentState = null;
    }
}