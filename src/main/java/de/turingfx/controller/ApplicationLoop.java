package de.turingfx.controller;

import de.turingfx.model.exceptions.TuringMachineException;
import de.turingfx.model.exceptions.UndefinedStateTransitionException;
import de.turingfx.model.state.StateStatus;
import javafx.animation.AnimationTimer;
import javafx.util.Duration;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @author Lam, Le (msg systems ag) 2018
 */
@Slf4j
public class ApplicationLoop extends AnimationTimer {

    private TuringMachine turingMachine;
    private double durationTime = 5000;
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
                    case END_STATE_REACHED:
                        stop();
                        break;
                    default:
                }
            } catch (UndefinedStateTransitionException | TuringMachineException e) {
                stop();
                MaintainControllerInstances.getMainController()
                        .throwErrorDialog(e.getLocalizedMessage());
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