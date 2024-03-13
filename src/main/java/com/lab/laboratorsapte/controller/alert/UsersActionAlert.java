package com.lab.laboratorsapte.controller.alert;

import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class UsersActionAlert {

    public static void showMessage(Stage owner, Alert.AlertType alertType, String title, String description){
        Alert message = new Alert(alertType);
        message.initOwner(owner);
        message.setTitle(title);
        message.setContentText(description);
        message.showAndWait();
    }

}
