package com.lab.laboratorsapte;

import com.lab.laboratorsapte.controller.AdminController;
import com.lab.laboratorsapte.repository.DBFriendshipsRepo;
import com.lab.laboratorsapte.repository.DBMessagesRepo;
import com.lab.laboratorsapte.repository.DBUsersRepo;
import com.lab.laboratorsapte.service.FriendshipService;
import com.lab.laboratorsapte.service.MessageService;
import com.lab.laboratorsapte.service.UserService;
import com.lab.laboratorsapte.ui.UI;
import com.lab.laboratorsapte.ui.UiImplementation;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;

public class AdminApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        //this.Tests();
        FXMLLoader fxmlLoader = new FXMLLoader(AdminApplication.class.getResource("admin.fxml"));
        DBUsersRepo repo = new DBUsersRepo("jdbc:postgresql://localhost:5432/socialnetwork", "postgres", "17072003");
        UserService service = new UserService(repo);

        DBFriendshipsRepo repof = new DBFriendshipsRepo("jdbc:postgresql://localhost:5432/socialnetwork", "postgres", "17072003");
        FriendshipService serv = new FriendshipService(repof);

        DBMessagesRepo repom = new DBMessagesRepo("jdbc:postgresql://localhost:5432/socialnetwork", "postgres", "17072003", repo);
        MessageService servm = new MessageService(repom, repo);

        UI ui = new UiImplementation(service, serv, servm);
        ui.run();

        AnchorPane root = fxmlLoader.load();
        Image x = new Image("C:/Users/Master/Downloads/backTest.png");
        BackgroundImage i = new BackgroundImage(
                x,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(100, 100, true, true, true, true)
        );
        Background bg = new Background(i);

        root.getStylesheets().add(getClass().getClassLoader().getResource("com/lab/laboratorsapte/styles.css").toExternalForm());
        root.setBackground(bg);
        Scene scene = new Scene(root, 600, 400);
        stage.setTitle("Admin");
        stage.setFullScreen(true);
        stage.setScene(scene);


        AdminController controller = fxmlLoader.getController();
        controller.setUserTaskService(service);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}

