package de.turingfx.model.state;

import de.turingfx.model.exceptions.CmdException;
import de.turingfx.model.CmdLine;
import de.turingfx.model.turing.Movement;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * @author Lam, Le (msg systems ag) 2018
 */
public class StateRegisterUnitTest {

    @Test
    void shouldDetermineAllStates() throws CmdException {
        // given
        StateRegister stateService = new StateRegister();
        String startStr = "START";
        String endStr = "END";
        String normalStr = "A1";

        State start = State.builder().transitionsActions(new HashMap<>())
                .transitionsState(new HashMap<>()).transitionsWrite(new HashMap<>())
                .name(startStr).stateType(StateType.START).build();
        State normal = State.builder().transitionsActions(new HashMap<>())
                .transitionsState(new HashMap<>()).transitionsWrite(new HashMap<>())
                .name(normalStr).stateType(StateType.NORMAL).build();
        State end = State.builder().transitionsActions(new HashMap<>())
                .transitionsState(new HashMap<>())
                .transitionsWrite(new HashMap<>())
                .name(endStr).stateType(StateType.END).build();

        CmdLine firstLine = CmdLine.builder()
                .currentState(start).readAlphabet("a")
                .writeAlphabet("b").nextState(normal)
                .movements(Arrays.asList(Movement.RIGHT))
                .build();

        CmdLine secondLine = CmdLine.builder()
                .currentState(normal).readAlphabet("b")
                .writeAlphabet("c").nextState(end)
                .movements(Arrays.asList(Movement.NEUTRAL))
                .build();

        List<CmdLine> cmdLines = Arrays.asList(firstLine, secondLine);

        // when
        stateService.determineStates(cmdLines);

        // then
        Assertions.assertThat(stateService.getStateByName(startStr)).isNotNull();
        Assertions.assertThat(stateService.getStateByName(normalStr)).isNotNull();
        Assertions.assertThat(stateService.getStateByName(endStr)).isNotNull();
        Assertions.assertThat(stateService.getCurrentState().getStateType())
                .isEqualTo(StateType.START);
    }

    @Test
    void shouldTransitCorrectly() throws CmdException {
        // given
        StateRegister stateService = new StateRegister();
        String startMark = "START";
        String endMark = "END";
        String normalStr = "A1";

        State start = State.builder().transitionsActions(new HashMap<>())
                .transitionsState(new HashMap<>()).transitionsWrite(new HashMap<>())
                .name(startMark).stateType(StateType.START).build();
        State normal = State.builder().transitionsActions(new HashMap<>())
                .transitionsState(new HashMap<>()).transitionsWrite(new HashMap<>())
                .name(normalStr).stateType(StateType.NORMAL).build();
        State end = State.builder().transitionsActions(new HashMap<>())
                .transitionsState(new HashMap<>())
                .transitionsWrite(new HashMap<>())
                .name(endMark).stateType(StateType.END).build();

        CmdLine firstLine = CmdLine.builder()
                .currentState(start).readAlphabet("a")
                .writeAlphabet("b").nextState(normal).movements(Arrays.asList(Movement.RIGHT))
                .build();

        CmdLine secondLine = CmdLine.builder()
                .currentState(normal).readAlphabet("b")
                .writeAlphabet("c").nextState(end).movements(Arrays.asList(Movement.NEUTRAL))
                .build();

        List<CmdLine> cmdLines = Arrays.asList(firstLine, secondLine);

        // when
        stateService.determineStates(cmdLines);

        // then
        State startTr = stateService.getStateByName(startMark);
        String a = "a";
        Assertions.assertThat(startTr.getStateType()).isEqualTo(StateType.START);

        String normStr = startTr.getNext(a);
        String b = startTr.getNextWriteActions(a);
        Assertions.assertThat(startTr.getNextMovements(a).get(0)).isEqualTo(Movement.RIGHT);
        Assertions.assertThat(b).isEqualTo("b");

        State normTr = stateService.getStateByName(normStr);
        Assertions.assertThat(normTr.getStateType()).isEqualTo(StateType.NORMAL);

        String endStr = normTr.getNext(b);
        String c = normTr.getNextWriteActions(b);
        Assertions.assertThat(normTr.getNextMovements(b).get(0)).isEqualTo(Movement.NEUTRAL);

        Assertions.assertThat(c).isEqualTo("c");

        State endTr = stateService.getStateByName(endStr);
        Assertions.assertThat(endTr.getStateType()).isEqualTo(StateType.END);
        Assertions.assertThat(endTr.getName()).isEqualTo(endMark);

    }
}