package de.turingfx;

import de.turingfx.controller.MaintainControllerInstances;
import de.turingfx.controller.TuringMachine;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * @author Lam, Le (msg systems ag) 2018
 */
public class TuringMachineApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
        Parent root = loader.load();

        MaintainControllerInstances.setMainController(loader.getController());

        Scene scene = new Scene(root);

        primaryStage.initStyle(StageStyle.UTILITY);
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(t -> {
            Platform.exit();
            System.exit(0);
        });

        primaryStage.show();
    }

    @Override
    public void stop() {
        TuringMachine.getInstance().reset();
    }

    public static void main(String... args) {
        launch(args);
    }
}