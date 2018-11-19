package de.turingfx.model.turing;

import javafx.scene.paint.Color;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Lam, Le (msg systems ag) 2018
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cell {

    private Color fontColor = Color.BLACK;

    private Color color = Color.LIGHTBLUE;

    // default alphabet is blank
    private String alphabet = "#";

    private boolean isFlagged = false;
}