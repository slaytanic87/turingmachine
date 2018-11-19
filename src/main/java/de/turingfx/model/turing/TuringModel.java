package de.turingfx.model.turing;

import de.turingfx.model.exceptions.TuringMachineException;
import de.turingfx.model.exceptions.UndefinedStateTransitionException;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Lam, Le (msg systems ag) 2018
 */
@Data
public class TuringModel {

    private List<Tape> tapes = new ArrayList<>();

    public Tape getTape(int index) {
        return tapes.get(index);
    }

    public void addTape(Tape tape) {
        tapes.add(tape);
    }

    public void clearTapes() {
        tapes = new ArrayList<>();
    }

    public void moveHeads(List<Movement> movements) throws UndefinedStateTransitionException {
        if (movements.size() != tapes.size()) {
            throw new RuntimeException("The numer of movements ("
                    + movements.size() + ") are not equals the number of tapes ("
                    + tapes.size() + ")");
        }
        for (int i = 0; i < tapes.size(); i++) {
            switch (movements.get(i)) {
                case LEFT:
                    tapes.get(i).moveLeft();
                    break;
                case RIGHT:
                    tapes.get(i).moveRight();
                    break;
                case NEUTRAL:
                default:
            }
        }
    }

    public String getCurrentCellsInput() throws TuringMachineException {
        StringBuilder input = new StringBuilder();
        for (Tape tape: tapes) {
            input.append(tape.getCellAtCurrentPosition().getAlphabet());
        }
        return input.toString();
    }

    public void writeCurrentCells(String input) throws TuringMachineException {
        char[] alphabets = input.toCharArray();
        for (int i = 0; i < tapes.size(); i++) {
            tapes.get(i).getCellAtCurrentPosition().setAlphabet(String.valueOf(alphabets[i]));
        }
    }
}