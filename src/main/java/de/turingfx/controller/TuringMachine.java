package de.turingfx.controller;

import de.turingfx.model.CmdLine;
import de.turingfx.model.exceptions.CmdException;
import de.turingfx.model.exceptions.TuringMachineException;
import de.turingfx.model.exceptions.UndefinedStateTransitionException;
import de.turingfx.model.state.State;
import de.turingfx.model.state.StateRegister;
import de.turingfx.model.state.StateStatus;
import de.turingfx.model.state.StateType;
import de.turingfx.model.turing.Movement;
import de.turingfx.model.turing.Tape;
import de.turingfx.model.turing.TuringModel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * This class represents a deterministic turing machine.
 * @author Lam, Le (msg systems ag) 2018
 */
@Slf4j
public class TuringMachine {

    public static final int MAX_TAPE_SIZE = 7;

    private int numberOfTapes = 0;
    private static TuringMachine INSTANCE = null;

    @Getter
    private TuringModel turingModel;

    private CommandParser commandParser;

    private StateRegister stateService;

    private TuringMachine() {
        turingModel = new TuringModel();
        commandParser = new CommandParser();
        stateService = new StateRegister();
    }

    public static TuringMachine getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TuringMachine();
        }
        return INSTANCE;
    }

    public List<State> getStates() {
        return this.stateService.getStates();
    }

    public State getCurrentState() {
        return stateService.getCurrentState();
    }

    public void setNumberOfTapes(int numberOfTapes) {
        this.numberOfTapes = numberOfTapes;
        commandParser.setNumberOfTapes(numberOfTapes);
        turingModel.clearTapes();
        for (int i = 0; i < numberOfTapes; i++) {
            turingModel.addTape(new Tape());
        }
    }

    public void reset() {
        turingModel.clearTapes();
        stateService.reset();
        this.numberOfTapes = 0;
        commandParser.setNumberOfTapes(this.numberOfTapes);
    }

    public boolean addTape(String input) throws TuringMachineException {
        if (this.numberOfTapes == MAX_TAPE_SIZE) {
            return false;
        }

        Tape tape = new Tape();
        tape.setName(String.valueOf(this.numberOfTapes));
        tape.parseInput(input);
        tape.setPosition(0);

        this.numberOfTapes++;
        commandParser.setNumberOfTapes(this.numberOfTapes);
        turingModel.addTape(tape);

        return true;
    }

    public int getNumberOfTapes() {
        return this.numberOfTapes;
    }

    public StateStatus iterateStep() throws UndefinedStateTransitionException,
                                            TuringMachineException {
        if (numberOfTapes == 0) {
            log.debug("The number of tapes are not set! {}", numberOfTapes);
            throw new TuringMachineException("The number of tapes are not set!");
        }

        log.debug("Read current state: {}", stateService.getCurrentState());

        if (stateService.getCurrentState().getStateType().equals(StateType.END)) {
            return StateStatus.END_STATE_REACHED;
        }

        String currentCellsInput = turingModel.getCurrentCellsInput();
        log.debug("Read current input: {}", currentCellsInput);

        List<Movement> nextHeadMovements = stateService.getNextMovements(currentCellsInput);
        String nextWriteAlphabet = stateService.getNextWriteAlphabets(currentCellsInput);
        State nextState = stateService.getNextState(currentCellsInput);

        log.debug("Next head movements: {}", nextHeadMovements);
        log.debug("Next alphabets to be written: {}", nextWriteAlphabet);
        log.debug("Enter next state: {}", nextState);

        if (nextState.getStateType().equals(StateType.NORMAL) &&
                StringUtils.isBlank(nextWriteAlphabet)) {
            log.debug("Alphabet to be written are not present for input: {}", currentCellsInput);
            throw new UndefinedStateTransitionException(currentCellsInput);
        }

        if (nextState.getStateType().equals(StateType.NORMAL) &&
                (nextHeadMovements == null || nextHeadMovements.size() == 0)) {
            log.debug("Head movements for the next state are not present for input: {}", currentCellsInput);
            throw new UndefinedStateTransitionException(currentCellsInput);
        }

        stateService.setCurrentState(nextState);
        turingModel.writeCurrentCells(nextWriteAlphabet);
        turingModel.moveHeads(nextHeadMovements);

        if (nextState.getStateType().equals(StateType.END)) {
            log.debug("End state reached!");
            return StateStatus.END_STATE_REACHED;
        }

        log.debug("Ready for next state...");
        return StateStatus.NEXT_STATE_AVAILABLE;
    }

    public void parseAndDetermineState(String cmdLines) throws CmdException {
        List<CmdLine> commands = commandParser.parseCommandLines(cmdLines);
        stateService.determineStates(commands);
    }
}