package com.lab.laboratorsapte;

import com.lab.laboratorsapte.controller.LoginController;
import com.lab.laboratorsapte.repository.DBFriendRequestsRepo;
import com.lab.laboratorsapte.repository.DBFriendshipsRepo;
import com.lab.laboratorsapte.repository.DBMessagesRepo;
import com.lab.laboratorsapte.repository.DBUsersRepo;
import com.lab.laboratorsapte.service.FriendRequestService;
import com.lab.laboratorsapte.service.FriendshipService;
import com.lab.laboratorsapte.service.MessageService;
import com.lab.laboratorsapte.service.UserService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;


public class LoginApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(LoginApplication.class.getResource("login.fxml"));

        DBUsersRepo repo =new DBUsersRepo("jdbc:postgresql://localhost:5432/socialnetwork","postgres","17072003");
        UserService servu = new UserService(repo);
        DBFriendshipsRepo repof = new DBFriendshipsRepo("jdbc:postgresql://localhost:5432/socialnetwork","postgres","17072003");
        FriendshipService servf = new FriendshipService(repof);
        DBMessagesRepo repom = new DBMessagesRepo("jdbc:postgresql://localhost:5432/socialnetwork","postgres","17072003", repo);
        MessageService servm = new MessageService(repom,repo);
        DBFriendRequestsRepo repofr = new DBFriendRequestsRepo("jdbc:postgresql://localhost:5432/socialnetwork","postgres","17072003");
        FriendRequestService servffr = new FriendRequestService(repofr,repo,repof);


        TabPane root = fxmlLoader.load();

        root.getStylesheets().add(getClass().getClassLoader().getResource("com/lab/laboratorsapte/styles.css").toExternalForm());
        Scene scene = new Scene(root, 600, 400);
        primaryStage.setTitle("User");
        primaryStage.setScene(scene);

        LoginController controller = fxmlLoader.getController();
        controller.setService(servu, servf, servm, servffr);
        primaryStage.show();
    }
}
