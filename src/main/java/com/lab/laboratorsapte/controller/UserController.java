package com.lab.laboratorsapte.controller;

import com.lab.laboratorsapte.controller.alert.FriendRequestActionAlert;
import com.lab.laboratorsapte.controller.alert.MessageActionAlert;
import com.lab.laboratorsapte.controller.alert.UsersActionAlert;
import com.lab.laboratorsapte.domain.*;
import com.lab.laboratorsapte.service.FriendRequestService;
import com.lab.laboratorsapte.service.FriendshipService;
import com.lab.laboratorsapte.service.MessageService;
import com.lab.laboratorsapte.service.UserService;
import com.lab.laboratorsapte.utils.events.*;
import com.lab.laboratorsapte.utils.observer.Observer;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class UserController implements Observer<UserTaskChangeEvent> {

    private Long id_user;

    private FriendRequestService friendRequestService;
    private UserService userService;
    private FriendshipService friendshipService;
    private MessageService mesageService;

    ObservableList<User> modelfriends = FXCollections.observableArrayList();
    ObservableList<User> modelrequests = FXCollections.observableArrayList();
    private ObservableList modelMessage = FXCollections.observableArrayList();

    @FXML
    ListView<Message> listMessages;
    @FXML
    public TableView<User> tableFriends;

    @FXML
    public TableView<User> tableFriendsRequests;

    @FXML
    public TableView<User> table_list_friends;

    @FXML
    public TableColumn<User, String> tableColumnFN;
    @FXML
    public TableColumn<User, String> tableColumnLN;

    @FXML
    public TableColumn<User, String> tableColumnFN2;
    @FXML
    public TableColumn<User, String> tableColumnLN2;

    @FXML
    public TableColumn<User, String> tableColumnFN3;
    @FXML
    public TableColumn<User, String> tableColumnLN3;

    @FXML
    public TextField text_fn;

    @FXML
    public TextField text_ln ;

    @FXML
    public TextField message;

    @FXML
    public ImageView imageView;
    @FXML
    public TextField fn_account;

    @FXML TextField ln_account;

    @FXML
    public Button add;

    @FXML
    public Button send;

    @FXML
    public Button exit1;

    public void setService(Long id, FriendRequestService friendRequestService, UserService userService, FriendshipService friendshipService, MessageService mesageService){
        this.id_user = id;
        this.friendRequestService = friendRequestService;
        this.userService = userService;
        this.friendshipService = friendshipService;
        this.mesageService = mesageService;
        userService.addObserver(this);
        friendshipService.addObserver(this);
        friendRequestService.addObserver(this);
        mesageService.addObserver(this);
        initializeTableFriends();
        initializeTableFriendRequests();
        account();
    }
    
    @Override
    public void update(UserTaskChangeEvent userTaskChangeEvent) {
        initializeTableFriends();
        initializeTableFriendRequests();
        handleSelect();
    }

    public void initializeTableFriends(){
        List<Long> friends = userService.get_friends(id_user);
        Iterable<User> allFriendsUser = friends.stream()
                .map(x -> {return userService.getEntityById(x).get();})
                .collect(Collectors.toList());

        List<User> allFriend = StreamSupport.stream(allFriendsUser.spliterator(), false).toList();
        modelfriends.setAll(allFriend);
    }

    public void initializeTableFriendRequests(){
        List<Long> friendRequests = friendRequestService.getFriendRequestIds(id_user);
        Iterable<User> allFriendRequestsUser = friendRequests.stream()
                .map(x -> {return userService.getEntityById(x).get();})
                .collect(Collectors.toList());

        List<User> allFriendRequests = StreamSupport.stream(allFriendRequestsUser.spliterator(), false).toList();
        modelrequests.setAll(allFriendRequests);
    }

    public void account ()
    {
        Optional<User> u = userService.getEntityById(id_user);
        fn_account.setText(u.get().getFirstname());
        ln_account.setText(u.get().getLastname());
        fn_account.setEditable(false);
        ln_account.setEditable(false);
    }

    @FXML
    public void initialize() {
        tableColumnFN.setCellValueFactory(new PropertyValueFactory<User, String>("firstname"));
        tableColumnLN.setCellValueFactory(new PropertyValueFactory<User, String>("lastname"));
        tableFriends.setItems(modelfriends);

        tableColumnFN2.setCellValueFactory(new PropertyValueFactory<User, String>("firstname"));
        tableColumnLN2.setCellValueFactory(new PropertyValueFactory<User, String>("lastname"));
        tableFriendsRequests.setItems(modelrequests);

        tableColumnFN3.setCellValueFactory(new PropertyValueFactory<User, String>("firstname"));
        tableColumnLN3.setCellValueFactory(new PropertyValueFactory<User, String>("lastname"));

        table_list_friends.setItems(modelfriends);

        table_list_friends.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    public void handleAccept(){
        User User = tableFriendsRequests.getSelectionModel().getSelectedItem();
        Tuple<Long, Long> ids = new Tuple<>(User.getId(), id_user);

        FriendRequest friendRequest = friendRequestService.getEntityById(ids).get();
        friendRequest.setStatus(FriendRequestStatus.APPROVED);
        friendRequestService.update(friendRequest);

        Friendship Friendship = new Friendship(LocalDateTime.now());
        Friendship.setId(ids);
        friendshipService.add(Friendship);
        friendRequestService.notifyObservers(new FriendRequestTaskChangeEvent(ChangeEventType.ADD, friendRequest));
        update(new FriendshipTaskChangeEvent(ChangeEventType.ADD, Friendship));
    }

    public void handleDelete(){
        User User = tableFriendsRequests.getSelectionModel().getSelectedItem();
        Tuple<Long, Long> ids = new Tuple<>(User.getId(), id_user);

        FriendRequest friendRequest = friendRequestService.getEntityById(ids).get();
        friendRequest.setStatus(FriendRequestStatus.REJECTED);
        friendRequestService.update(friendRequest);
        friendRequestService.notifyObservers(new FriendRequestTaskChangeEvent(ChangeEventType.ADD, friendRequest));

        update(new FriendRequestTaskChangeEvent(ChangeEventType.DELETE, friendRequest));
    }

    public void handle_add_friend(){
        String first_name = text_fn.getText();
        String last_name = text_ln.getText();

        if(first_name == null || last_name == null)
        {
            UsersActionAlert.showMessage(null,Alert.AlertType.ERROR,"Error","Eroare! Trebuie sa completezi campurile!");
            return;
        }
        Optional<User> user = userService.findUserByName(first_name,last_name);
        if(user.isEmpty()) {
            FriendRequestActionAlert.showMessage(null, Alert.AlertType.ERROR, "Error", "Eroare! Userul introdus nu exista!");
            return;
        }

        FriendRequestStatus friendRequestStatus = FriendRequestStatus.PENDING;
        FriendRequest friendRequest = new FriendRequest(friendRequestStatus);
        friendRequest.setId(new Tuple<>(id_user, user.get().getId()));

        try {
            Optional<FriendRequest> sentRequest = friendRequestService.add(friendRequest);
            if(sentRequest.isPresent())
                FriendRequestActionAlert.showMessage(null, Alert.AlertType.ERROR, "Error", "Exista deja o cerere catre Userul dat!");
            else {
                FriendRequestActionAlert.showMessage(null, Alert.AlertType.INFORMATION, "Succes!", "Cererea a fost trimisa cu succes!");
                friendRequestService.notifyObservers(new FriendRequestTaskChangeEvent(ChangeEventType.ADD, friendRequest));
                Platform.runLater(()->update(new FriendRequestTaskChangeEvent(ChangeEventType.ADD, friendRequest)));
            }
        }
        catch (Exception e){
            FriendRequestActionAlert.showMessage(null, Alert.AlertType.ERROR, "Error", e.getMessage());}

        text_fn.clear();
        text_ln.clear();
    }

    private void loadListOfMessages(Long userIdFrom, Long userIdTo) {
        listMessages.getItems().clear();
        modelMessage.clear();
        for (Message msg : mesageService.getMessagesBetweenTwoUsers(userIdFrom, userIdTo)) {
            modelMessage.add(msg);
        }

        listMessages.setItems(modelMessage);
    }

    public void handleSend(){
        List<User> prieteni = table_list_friends.getSelectionModel().getSelectedItems();


        if(prieteni.isEmpty()){
            MessageActionAlert.showMessage(null, Alert.AlertType.ERROR, "Error", "Trebuie selectat un email!");
            return;
        }
        String text = message.getText();
        if(text.isEmpty()){
            MessageActionAlert.showMessage(null, Alert.AlertType.ERROR, "Error", "Mesajul nu poate sa fie gol!");
            return;
        }

        List<User> Useri = prieteni.stream()
                .map(x -> {return userService.findUserByName(x.getFirstname(),x.getLastname()).get();})
                .toList();
        Useri.forEach(x ->
        { mesageService.addMessage(id_user, x.getId(), text); });


        if(Useri.size() == 1) {
            loadListOfMessages(id_user, Useri.get(0).getId());
            mesageService.notifyObservers(new MessageTaskChangeEvent(ChangeEventType.ADD, null));
            Platform.runLater(() -> update(new MessageTaskChangeEvent(ChangeEventType.ADD, null)));

        }
        message.clear();
    }

    public void handleSelect(){
        List<User> Users = table_list_friends.getSelectionModel().getSelectedItems();
        if(Users.size() == 1) {
            User User = userService.findUserByName(Users.get(0).getFirstname(),Users.get(0).getLastname()).get();
            loadListOfMessages(id_user, User.getId());
        }
        else {
            listMessages.getItems().clear();
            modelMessage.clear();
        }
    }
    public void handle_exit1(){
        Node src = exit1;
        Stage stage = (Stage) src.getScene().getWindow();
        stage.close();
    }
}
