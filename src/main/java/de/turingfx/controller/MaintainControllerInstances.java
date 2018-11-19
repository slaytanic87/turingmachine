package de.turingfx.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * @author Lam, Le (msg systems ag) 2018
 */
public class MaintainControllerInstances {

    private static MainController mainController;
    private static InfoController infoController;
    public static MainController getMainController() {
        return mainController;
    }

    public static void loadInfoController() {
        FXMLLoader fxmlLoader = new FXMLLoader(MaintainControllerInstances.class.getResource("/fxml/info.fxml"));
        try {
            Parent root = fxmlLoader.load();
            if (infoController == null) {
                infoController = fxmlLoader.getController();
            }
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
//            stage.initStyle(StageStyle.UNDECORATED);
            stage.setResizable(false);
            stage.setTitle("Info");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void setMainController(MainController controller) {
        mainController = controller;
    }
}