package com.lab.laboratorsapte.controller;

import com.lab.laboratorsapte.AdminApplication;
import com.lab.laboratorsapte.UserApplication;
import com.lab.laboratorsapte.controller.alert.LoginActionAlert;
import com.lab.laboratorsapte.domain.User;
import com.lab.laboratorsapte.service.FriendRequestService;
import com.lab.laboratorsapte.service.FriendshipService;
import com.lab.laboratorsapte.service.MessageService;
import com.lab.laboratorsapte.service.UserService;
import com.lab.laboratorsapte.ui.UI;
import com.lab.laboratorsapte.ui.UiImplementation;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class LoginController {

    private UserService userService;
    private FriendshipService friendshipService;
    private FriendRequestService friendRequestService;
    private MessageService medsageService;

    @FXML
    private TextField idField;

    @FXML
    private Button loginButton;

    @FXML
    private TextField password;

    @FXML
    private Button exitButton;

    @FXML
    private ImageView imageView;

    @FXML
    private Label label;

    @FXML
    private ImageView imageView2;

    @FXML
    private TextField idS;
    @FXML
    private TextField fnS;
    @FXML
    private TextField lnS;
    @FXML
    private TextField emS;
    @FXML
    private TextField paS;

    public void setService(UserService u, FriendshipService f, MessageService m, FriendRequestService fr){
        userService = u;
        friendshipService = f;
        friendRequestService = fr;
        medsageService = m;
    }

    public void handleCancel(){
        Node src = exitButton;
        Stage stage = (Stage) src.getScene().getWindow();
        stage.close();
    }

    public void handleLogin(){
        String id = idField.getText();
        String pass = password.getText();
        if(id.equals("1"))
        {
            try{
                userService.tryLogin(id,pass);
                Stage stage = new Stage();
                FXMLLoader fxmlLoader = new FXMLLoader(AdminApplication.class.getResource("admin.fxml"));
                try{
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
                    controller.setUserTaskService(userService);
                    UI ui = new UiImplementation(userService,friendshipService,medsageService);
                    ui.run();
                    stage.show();

                } catch(IOException e){
                    LoginActionAlert.showMessage(null, Alert.AlertType.ERROR,"Error",e.getMessage());
                }
            }catch (RuntimeException e){
                LoginActionAlert.showMessage(null, Alert.AlertType.ERROR, "Eroare",e.getMessage());
            }

        }else {
            if(id.isEmpty()){
                LoginActionAlert.showMessage(null, Alert.AlertType.ERROR, "Error", "Eroare! Parola  nu poate sa fie nula!");
                idField.clear();
                return;
            }
            try{
                userService.tryLogin(id,pass);
                Long ID =Long.parseLong(id);
                Optional<User> u =userService.getEntityById(ID);
                if(u.isPresent()){
                    Stage stage = new Stage();
                    FXMLLoader fxmlLoader = new FXMLLoader(AdminApplication.class.getResource("user.fxml"));
                    try{
                        TabPane root = fxmlLoader.load();
                        root.getStylesheets().add(getClass().getClassLoader().getResource("com/lab/laboratorsapte/styles.css").toExternalForm());
                        Scene scene = new Scene(root, 600, 400);
                        stage.setTitle("User");
                        stage.setScene(scene);

                        UserController userController = fxmlLoader.getController();
                        userController.setService(ID, friendRequestService, userService, friendshipService, medsageService);
                        stage.show();
                    }catch (IOException e)
                    {
                        LoginActionAlert.showMessage(null, Alert.AlertType.ERROR,"Error",e.getMessage());
                    }

                }
            }catch (Exception e){
                LoginActionAlert.showMessage(null, Alert.AlertType.ERROR,"Eroare",e.getMessage());
            }
        }
    }

    public void handleSignUp(){
        String id = idS.getText();
        String fn = fnS.getText();
        String ln = lnS.getText();
        String em = emS.getText();
        String pa = paS.getText();

        try{
            userService.add(new User(fn,ln,em,Long.parseLong(id), userService.encrypt(pa)));
            idS.clear();
            fnS.clear();
            lnS.clear();
            emS.clear();
            paS.clear();
        }catch (Exception e)
        {
            LoginActionAlert.showMessage(null, Alert.AlertType.ERROR, "Error",e.getMessage());
        }

    }

}
