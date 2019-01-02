package de.turingfx.model.turing;

import de.turingfx.model.exceptions.TuringMachineException;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Lam, Le (msg systems ag) 2018
 */
@Data
public class Tape {

    public static final int MAX_VISIBLE_CELLS = 20;

    private int viewRangeMinPos;
    private int viewRangeMaxPos;

    private String name = "";
    private List<Cell> cells = new ArrayList<>();
    private int position = 0;

    public Tape() {
        viewRangeMinPos = position;
        viewRangeMaxPos = MAX_VISIBLE_CELLS - 1;
    }

    public List<Cell> getCellsInRange() throws TuringMachineException {
        checkRangeDataBound();
        List<Cell> range = new ArrayList<>();
        for (int i = viewRangeMinPos; i <= viewRangeMaxPos; i++) {
            range.add(cells.get(i));
        }
        return range;
    }

    private void checkRangeDataBound() throws TuringMachineException {
        if (viewRangeMinPos > position || position > viewRangeMaxPos) {
            throw new TuringMachineException("Tage: " + name + " head position is out of bound min: "
                    + viewRangeMinPos
                    + " max: " + viewRangeMaxPos
                    + " current: " + position);
        }
        if (viewRangeMaxPos > cells.size() - 1 ) {
            throw new TuringMachineException("Range max is bigger than number of cells max: "
                    + viewRangeMaxPos + " cells: " + cells.size());
        }
    }

    public void moveRight() {
        // expand tape to the right side
        if (position == cells.size() - 1) {
            cells.add(new Cell());
        }
        if (viewRangeMaxPos == position) {
            cells.get(position).setFlagged(false);
            position++;
            cells.get(position).setFlagged(true);
            viewRangeMinPos++;
            viewRangeMaxPos++;
            return;
        }
        cells.get(position).setFlagged(false);
        position++;
        cells.get(position).setFlagged(true);
    }

    public void moveLeft() {
        // expand tape to the left side
        if (position == 0) {
            cells.get(position).setFlagged(false);
            cells.add(0, new Cell());
            cells.get(position).setFlagged(true);
            return;
        }
        if (position == viewRangeMinPos) {
            viewRangeMinPos--;
            viewRangeMaxPos--;
        }
        cells.get(position).setFlagged(false);
        position--;
        cells.get(position).setFlagged(true);
    }

    public Cell getCell(int index) throws TuringMachineException {
        if (cells.size() <= index || index < 0) {
            throw new TuringMachineException("Out of bound access for index: "
                    + index + " and tape size: "
                    + cells.size());
        }
        return cells.get(index);
    }

    public Cell getCellAtCurrentPosition() throws TuringMachineException {
        return getCell(position);
    }

    public void setCells(List<Cell> cells) {
        this.cells = cells;
    }

    public void setPosition(int position) throws TuringMachineException {
        if (position < 0 || this.cells.size() == 0 || position > (this.cells.size() - 1)) {
            throw new TuringMachineException("Out of bound for position: " + position);
        }
        if (position < viewRangeMinPos) {
            throw new TuringMachineException("Out of view range! min range is " + viewRangeMinPos
            + " but position was " + position);
        }
        if (position > viewRangeMaxPos) {
            throw new TuringMachineException("Out of view range! max range is " + viewRangeMaxPos
                    + " but position was " + position);
        }

        cells.get(this.position).setFlagged(false);
        this.position = position;
        getCell(position).setFlagged(true);
    }

    public void parseInput(String input) {
        for (char c: input.toCharArray()) {
            Cell cell = new Cell();
            cell.setAlphabet(String.valueOf(c));
            cells.add(cell);
        }
        if (cells.size() < MAX_VISIBLE_CELLS) {
            for (int i = cells.size(); i < MAX_VISIBLE_CELLS; i++) {
                cells.add(new Cell());
            }
        }
    }
}