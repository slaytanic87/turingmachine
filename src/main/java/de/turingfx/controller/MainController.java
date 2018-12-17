package de.turingfx.controller;

import de.turingfx.model.exceptions.CmdException;
import de.turingfx.model.exceptions.TuringMachineException;
import de.turingfx.model.exceptions.UndefinedStateTransitionException;
import de.turingfx.model.state.State;
import de.turingfx.model.state.StateStatus;
import de.turingfx.model.turing.Cell;
import de.turingfx.model.turing.Tape;
import de.turingfx.model.turing.TuringModel;
import de.turingfx.view.HeadAnimation;
import de.turingfx.view.TapeInputDialog;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * @author Lam, Le (msg systems ag) 2018
 */
@Slf4j
public class MainController implements Initializable {

    public static final int CELL_SIZE = 30;
    public static final int PADDING_Y = 10;

    @FXML
    private Canvas trackCanvas;

    @FXML
    private TextArea txtAreaEditor;

    @FXML
    private Label lblCurrentState;

    @FXML
    private Label lblNumberOfTapes;

    @FXML
    private Label lblStatus;

    @FXML
    private TextField edtStates;

    @FXML
    private Button btnParse;

    @FXML
    private Button btnStep;

    @FXML
    private Button btnAddTape;

    @FXML
    private Button btnStart;

    @FXML
    private Button btnPause;

    private GraphicsContext trackGraphicsContext;

    private List<HeadAnimation> headAnimationList = new ArrayList<>();

    private ApplicationLoop applicationLoop;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        trackGraphicsContext = trackCanvas.getGraphicsContext2D();
        applicationLoop = new ApplicationLoop(TuringMachine.getInstance());
        applicationLoop.setDuration(Duration.millis(500));
        lblStatus.setTextFill(Color.RED);
        resetButtons();
    }

    public void drawRectWithContour(Point2D topLeftpos, Cell tapeContent) {
        // draw rect
        trackGraphicsContext.setFill(tapeContent.getColor());
        // shrink rect to visualize contour
        trackGraphicsContext.fillRect(topLeftpos.getX(), topLeftpos.getY(),
                CELL_SIZE - 2,
                CELL_SIZE - 2);

        drawCellTextValue(topLeftpos, tapeContent);
    }

    private void drawCellTextValue(Point2D topLeftPos, Cell tapeContent) {
        trackGraphicsContext.setFill(tapeContent.getFontColor());
        trackGraphicsContext.fillText(tapeContent.getAlphabet(),
                topLeftPos.getX() + (CELL_SIZE / 3),
                topLeftPos.getY() + (CELL_SIZE / 2) + 3);
    }

    public void clearFieldCanvas() {
        clearHeadAnimation();
        trackGraphicsContext.clearRect(0, 0, trackCanvas.getWidth(), trackCanvas.getHeight());
    }

    public void setStatesValue(List<State> stateList) {
        StringBuilder str = new StringBuilder();
        for (State state: stateList) {
            str.append(state.getName()).append(",");
        }
        edtStates.setText(str.toString());
    }

    private void drawTapes(TuringModel turingModel) throws TuringMachineException {
        final int spaceBetweenTapes = 30;
        Point2D pos = new Point2D(0, PADDING_Y);
        for (Tape tape: turingModel.getTapes()) {
            double cols = 0;
            for (Cell cell: tape.getCellsInRange()) {
                if (cell.isFlagged()) {
                    headAnimationList.add(new HeadAnimation()
                            .createHeadAnimation(trackGraphicsContext, pos, cell).play());
                    drawHeadPointer(pos);
                } else {
                    drawRectWithContour(pos, cell);
                }
                pos = pos.add(MainController.CELL_SIZE, 0);
                cols += MainController.CELL_SIZE;
            }
            pos = pos.add(0, MainController.CELL_SIZE + spaceBetweenTapes);
            pos = pos.subtract(cols, 0);
        }
    }

    private void drawHeadPointer(Point2D pos) {
        final int wx = MainController.CELL_SIZE / 2;
        final int wy = MainController.CELL_SIZE;
        final int headHeight = 10;

        double nodeX = pos.getX() + wx;
        double nodeY = pos.getY() + wy + headHeight;

        // draw head
        trackGraphicsContext.setFill(Color.BLACK);
        trackGraphicsContext.setLineWidth(2.0);
        trackGraphicsContext.strokeLine(pos.getX() + wx, pos.getY() + wy, nodeX, nodeY);

        // draw line
        int boxPosX = Tape.MAX_VISIBLE_CELLS * CELL_SIZE + 10;
        trackGraphicsContext.strokeLine(nodeX, nodeY, boxPosX, nodeY);
    }

    private void drawBox() {
        double height = trackCanvas.getHeight();
        int tapeWidth = Tape.MAX_VISIBLE_CELLS * CELL_SIZE + 10;
        final double boxWidth = 45;
        final double boxHeight = height - PADDING_Y * 2;

        trackGraphicsContext.setFill(Color.BLACK);
        trackGraphicsContext.fillRect(tapeWidth, PADDING_Y, boxWidth, boxHeight);

        trackGraphicsContext.setFill(Color.WHITESMOKE);
        trackGraphicsContext.fillRect(tapeWidth + 2, PADDING_Y + 2, boxWidth - 4, boxHeight - 4);

        trackGraphicsContext.setFill(Color.BLACK);
        Text boxName = new Text("DTM");
        double textWidth = boxName.getBoundsInLocal().getWidth();
        trackGraphicsContext.fillText(boxName.getText(), tapeWidth + (boxWidth - textWidth) / 2,
                PADDING_Y + (boxHeight / 2));
    }

    private void clearHeadAnimation() {
        for (HeadAnimation headAnimation: headAnimationList) {
            headAnimation.stop();
        }
        headAnimationList.clear();
    }

    public void drawField() throws TuringMachineException {
        clearFieldCanvas();
        drawTapes(TuringMachine.getInstance().getTuringModel());
        drawBox();
    }

    @FXML
    private void onStepClick(MouseEvent mouseEvent) {
        log.debug("on step clicked!");
        try {
            StateStatus stateStatus = TuringMachine.getInstance().iterateStep();
            drawField();
            switch (stateStatus) {
                case NEXT_STATE_AVAILABLE:
                    btnParse.setDisable(true);
                    break;
                case END_STATE_REACHED:
                case INTERNAL_ERROR:
                case DECLINED:
                    btnParse.setDisable(false);
                    break;
                default:
                    throw new TuringMachineException("Unknown state status: " + stateStatus);
            }
            setLabels(stateStatus, Color.GREEN);
        } catch (UndefinedStateTransitionException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("State trasition error");
            alert.setContentText(e.getLocalizedMessage());
            alert.showAndWait();
            setLabels(StateStatus.DECLINED, Color.RED);
        } catch (TuringMachineException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Processing error");
            alert.setContentText(e.getLocalizedMessage());
            alert.showAndWait();
            setLabels(StateStatus.INTERNAL_ERROR, Color.RED);
        }
    }

    public void setLabels(StateStatus stateStatus, Color color) {
        lblStatus.setTextFill(color);
        lblStatus.setText(stateStatus.getLabel());
        lblCurrentState.setText(TuringMachine.getInstance().getCurrentState().getName());
    }

    @FXML
    private void onParseClick(MouseEvent mouseEvent) {
        log.debug("on parse clicked!");
        try {
            TuringMachine.getInstance().parseAndDetermineState(txtAreaEditor.getText());
            setStatesValue(TuringMachine.getInstance().getStates());
            lblCurrentState.setText(TuringMachine.getInstance()
                    .getCurrentState().getName());
            btnAddTape.setDisable(true);
            btnStep.setDisable(false);
            btnStart.setDisable(false);
            btnPause.setDisable(false);
            txtAreaEditor.setEditable(false);
        } catch (CmdException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Parse error");
            alert.setContentText(e.getLocalizedMessage());
            alert.showAndWait();
            btnStep.setDisable(true);
            btnStart.setDisable(true);
            btnPause.setDisable(true);
            txtAreaEditor.setEditable(true);
        }
    }

    public void throwErrorDialog(String message, boolean showAndWait) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("An error occurred");
        alert.setContentText(message);
        if (showAndWait) {
            alert.showAndWait();
        } else {
            alert.show();
        }
    }


    @FXML
    private void onAddTape(MouseEvent mouseEvent) {
        log.debug("on add clicked!");
        TapeInputDialog tapeInputDialog = new TapeInputDialog()
                .withHeaderText("Tape");

        String input = tapeInputDialog.showAndWait().orElse(StringUtils.EMPTY);

        if (StringUtils.isEmpty(input)) {
            input = "#";
        }

        try {
            TuringMachine.getInstance().addTape(StringUtils.deleteWhitespace(input));
        } catch (TuringMachineException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Add tape error");
            alert.setContentText(e.getLocalizedMessage());
            alert.showAndWait();
        }

        if (TuringMachine.getInstance().getNumberOfTapes() == TuringMachine.MAX_TAPE_SIZE) {
            btnAddTape.setDisable(true);
        }

        if (TuringMachine.getInstance().getNumberOfTapes() > 0) {
            btnParse.setDisable(false);
        }

        lblNumberOfTapes.setText(String.valueOf(TuringMachine.getInstance()
                .getNumberOfTapes()));
        try {
            drawField();
        } catch (TuringMachineException e) {
            throwErrorDialog(e.getLocalizedMessage(), true);
        }
    }

    @FXML
    private void onStartAction(ActionEvent actionEvent) {
        log.debug("on start clicked!");
        applicationLoop.start();
    }

    @FXML
    private void onPauseAction(ActionEvent actionEvent) {
        log.debug("on stop clicked!");
        applicationLoop.stop();
    }

    @FXML
    private void onResetClick(MouseEvent mouseEvent) {
        log.debug("on reset clicked!");
        applicationLoop.stop();
        reset();
    }

    @FXML
    private void onOpenInfo(ActionEvent actionEvent) {
        MaintainControllerInstances.loadInfoController();
    }

    private void reset() {
        resetButtons();
        edtStates.clear();
        txtAreaEditor.setEditable(true);
        clearFieldCanvas();
        TuringMachine.getInstance().reset();
        lblCurrentState.setText("--");
        lblNumberOfTapes.setText(String.valueOf(TuringMachine.getInstance()
                .getNumberOfTapes()));
        lblStatus.setTextFill(Color.RED);
        lblStatus.setText("N/A");
    }

    private void resetButtons() {
        btnParse.setDisable(true);
        btnStep.setDisable(true);
        btnStart.setDisable(true);
        btnPause.setDisable(true);
        btnAddTape.setDisable(false);
    }
}