package de.turingfx.view;

import javafx.scene.control.TextInputDialog;

/**
 * @author Lam, Le (msg systems ag) 2018
 */
public class TapeInputDialog extends TextInputDialog {

    public TapeInputDialog() {
        super.setContentText("Enter input:");
    }

   public TapeInputDialog withTitle(String title) {
       super.setTitle(title);
       return this;
   }

   public TapeInputDialog withHeaderText(String headerText) {
       super.setHeaderText(headerText);
       return this;
   }

}
