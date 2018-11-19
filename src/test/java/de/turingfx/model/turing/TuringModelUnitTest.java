package de.turingfx.model.turing;

import de.turingfx.model.exceptions.TuringMachineException;
import de.turingfx.model.exceptions.UndefinedStateTransitionException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Lam, Le (msg systems ag) 2018
 */
public class TuringModelUnitTest {

    private TuringModel turingModel;

    @BeforeEach
    void setup() {
        turingModel = new TuringModel();
    }

    @Test
    void shouldMoveHeadAndPointCorrectly() throws TuringMachineException,
                                                  UndefinedStateTransitionException {
        // given
        Tape tape = new Tape();
        tape.setName(1 + "");
        tape.setCells(createCells(5));
        tape.getCell(1).setAlphabet("a");
        tape.getCell(2).setAlphabet("b");
        turingModel.addTape(tape);
        tape.setPosition(1);

        // when
        turingModel.moveHeads(Arrays.asList(Movement.RIGHT));
        // then
        Assertions.assertThat(turingModel.getCurrentCellsInput()).isEqualTo("b");

        // when
        turingModel.moveHeads(Arrays.asList(Movement.LEFT));
        // then
        Assertions.assertThat(turingModel.getCurrentCellsInput()).isEqualTo("a");

        // when
        turingModel.moveHeads(Arrays.asList(Movement.LEFT));
        // then
        Assertions.assertThat(turingModel.getCurrentCellsInput()).isEqualTo("#");

        // when
        turingModel.moveHeads(Arrays.asList(Movement.NEUTRAL));
        // then
        Assertions.assertThat(turingModel.getCurrentCellsInput()).isEqualTo("#");
    }

    private List<Cell> createCells(int numberOfCells) {
        List<Cell> cells = new LinkedList<>();
        for (int i = 0; i < numberOfCells; i++) {
            cells.add(new Cell());
        }
        return cells;
    }
}