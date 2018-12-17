package de.turingfx.controller;

import de.turingfx.model.exceptions.TuringMachineException;
import de.turingfx.model.exceptions.UndefinedStateTransitionException;
import de.turingfx.model.state.StateStatus;
import javafx.animation.AnimationTimer;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @author Lam, Le (msg systems ag) 2018
 */
@Slf4j
public class ApplicationLoop extends AnimationTimer {

    private TuringMachine turingMachine;
    private double durationTime = 1000000000;
    private long lastTime;

    public ApplicationLoop(TuringMachine turingMachine) {
        this.turingMachine = turingMachine;
    }

    @Override
    public void handle(long now) {
        double diff = now - lastTime;
        if (diff >= durationTime) {
            try {
                MaintainControllerInstances.getMainController().drawField();
                StateStatus stateStatus = turingMachine.iterateStep();
                switch (stateStatus) {
                    case INTERNAL_ERROR:
                    case DECLINED:
                        MaintainControllerInstances.getMainController().setLabels(stateStatus, Color.RED);
                        stop();
                        break;
                    case END_STATE_REACHED:
                        MaintainControllerInstances.getMainController().setLabels(stateStatus, Color.GREEN);
                        stop();
                        break;
                    case NEXT_STATE_AVAILABLE:
                        MaintainControllerInstances.getMainController().setLabels(stateStatus, Color.GREEN);
                        break;
                    default:
                        throw new TuringMachineException("Unknown state status: " + stateStatus);
                }
            } catch (UndefinedStateTransitionException | TuringMachineException e) {
                stop();
                MaintainControllerInstances.getMainController()
                        .throwErrorDialog(e.getLocalizedMessage(), false);
                MaintainControllerInstances.getMainController().setLabels(e instanceof TuringMachineException ?
                        StateStatus.INTERNAL_ERROR : StateStatus.DECLINED, Color.RED);
            }
            lastTime = now;
        }
    }

    public void setDuration(Duration duration) {
        durationTime = TimeUnit.MILLISECONDS
                .toNanos((long) duration.toMillis());
    }

    @Override
    public void stop() {
        super.stop();
    }

    @Override
    public void start() {
        super.start();
    }
}