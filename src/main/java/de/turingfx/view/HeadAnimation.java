package de.turingfx.view;

import de.turingfx.model.turing.Cell;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import static de.turingfx.controller.MainController.CELL_SIZE;

/**
 * @author Lam, Le (msg systems ag) 2018
 */
public class HeadAnimation {

    private Timeline headAnimation;

    public HeadAnimation createHeadAnimation(GraphicsContext graphicsContext, Point2D topLeftPos, Cell cellContent) {
        headAnimation = new Timeline();
        headAnimation.setCycleCount(Timeline.INDEFINITE);

        headAnimation.getKeyFrames().add(
                new KeyFrame(Duration.millis(200), actionEvent -> {
                    graphicsContext.setFill(Color.BLUE);
                    // shrink rect to visualize contour
                    graphicsContext.fillRect(topLeftPos.getX(), topLeftPos.getY(),
                            CELL_SIZE - 2,
                            CELL_SIZE - 2);
                    graphicsContext.setFill(cellContent.getFontColor());
                    graphicsContext.fillText(cellContent.getAlphabet(),
                            topLeftPos.getX() + (CELL_SIZE / 3),
                            topLeftPos.getY() + (CELL_SIZE / 2) + 3);
                }, new KeyValue[0]) // don't use binding
        );
        headAnimation.getKeyFrames().add(
                new KeyFrame(Duration.millis(400), actionEvent -> {
                    graphicsContext.setFill(Color.LIGHTBLUE);
                    graphicsContext.fillRect(topLeftPos.getX(), topLeftPos.getY(),
                            CELL_SIZE - 2,
                            CELL_SIZE - 2);
                    graphicsContext.setFill(cellContent.getFontColor());
                    graphicsContext.fillText(cellContent.getAlphabet(),
                            topLeftPos.getX() + (CELL_SIZE / 3),
                            topLeftPos.getY() + (CELL_SIZE / 2) + 3);
                }, new KeyValue[0]) // don't use binding);
        );
        return this;
    }

    public HeadAnimation play() {
        headAnimation.play();
        return this;
    }

    public HeadAnimation stop() {
        headAnimation.stop();
        return this;
    }

}