package com.lab.laboratorsapte.controller;

import com.lab.laboratorsapte.controller.alert.AdminActionAlert;
import com.lab.laboratorsapte.repository.Paginat.Page;
import com.lab.laboratorsapte.repository.Paginat.Pageable;
import com.lab.laboratorsapte.service.UserService;
import com.lab.laboratorsapte.utils.events.ChangeEventType;
import com.lab.laboratorsapte.utils.events.UserTaskChangeEvent;
import com.lab.laboratorsapte.utils.observer.Observer;
import com.lab.laboratorsapte.domain.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class AdminController implements Observer<UserTaskChangeEvent> {
    private UserService serv;
    ObservableList<User> model = FXCollections.observableArrayList();

    @FXML
    public TableView tableView;
    @FXML
    public TableColumn<User, Long> tableColumnID;
    @FXML
    public TableColumn<User, String> tableColumnFN;
    @FXML
    public TableColumn<User, String> tableColumnLN;
    @FXML
    public TableColumn<User, String> tableColumnE;

    @FXML
    public TableColumn<User, String> tableColumnP;


    @FXML
    public TextField TextID;

    @FXML
    public TextField TextFN;

    @FXML
    public TextField TextLN;

    @FXML
    public TextField TextE;

    @FXML
    public TextField TextP;

    @FXML
    public Button ExitButton;

    @FXML
    public Button OkButton;

    @FXML
    public Button BackButton;

    @FXML
    public Button NextButton;

    @FXML
    public TextField howMany;

    private int currentPage = 0;
    private int totalNumberOfElements = 0;

    @Override
    public void update(UserTaskChangeEvent userTaskChngeEvent) {
        initializeTableDataUser();
    }

    public void initializeTableDataUser(){
//        Iterable<User> all = serv.getAll();
//        List<User> allList = StreamSupport.stream(all.spliterator(), false).toList();
//        model.setAll(allList);
        String text = howMany.getText();
        try{
            Integer pageSize = Integer.parseInt(text);
            Page<User> page = serv.findAllPAGINAT(new Pageable(currentPage,pageSize));
            int maxPage = (int) Math.ceil((double)page.getTotalNumarElemente()/pageSize)-1;
            if(currentPage > maxPage)
            {
                currentPage = maxPage;
                page = serv.findAllPAGINAT(new Pageable(currentPage,pageSize));
            }
            model.setAll(StreamSupport.stream(page.getElementePePagina().spliterator(),false).collect(Collectors.toList()));
            totalNumberOfElements = page.getTotalNumarElemente();
            BackButton.setDisable(currentPage==0);
            NextButton.setDisable((currentPage+1)*pageSize >= totalNumberOfElements);
        }catch(NumberFormatException e)
        {
            System.out.println("1");
        }
    }

    public void setUserTaskService(UserService service){
        this.serv = service;
        serv.addObserver(this);
        //initializeTableDataUser();
    }

    @FXML
    public void initialize() {
        tableColumnID.setCellValueFactory(new PropertyValueFactory<User, Long>("id"));
        tableColumnFN.setCellValueFactory(new PropertyValueFactory<User, String>("firstname"));
        tableColumnLN.setCellValueFactory(new PropertyValueFactory<User, String>("lastname"));
        tableColumnE.setCellValueFactory(new PropertyValueFactory<User, String>("email"));
        tableColumnP.setCellValueFactory(new PropertyValueFactory<User, String>("password"));
        tableView.setItems(model);
    }

    public void onPrevious(ActionEvent actionEvent)
    {
        currentPage--;
        initializeTableDataUser();
    }

    public void onNext(ActionEvent actionEvent)
    {
        currentPage++;
        initializeTableDataUser();
    }

    public void handleAddUser()
    {
        String firstName = TextFN.getText();
        String lastName = TextLN.getText();
        String email = TextE.getText();
        String password = TextP.getText();
        try{
            Long id = Long.parseLong(TextID.getText());
            User user = new User(firstName,lastName,email,id,serv.encrypt(password));
            try{
                Optional<User> addUser = serv.add(user);
                if(addUser.isPresent())
                    AdminActionAlert.showMessage(null, Alert.AlertType.ERROR, "ERROR", "Eroare! Exista deja un User cu id-ul dat!");
                else{
                    update(new UserTaskChangeEvent(ChangeEventType.ADD,user));
                }
            }catch(Exception e){
                AdminActionAlert.showMessage(null, Alert.AlertType.ERROR, "ERROR", e.getMessage());
            }
        }catch(Exception e){
            AdminActionAlert.showMessage(null, Alert.AlertType.ERROR, "Error", "ID invalid! Id trebuie sa fie un numar!");
        }

        TextID.clear();
        TextFN.clear();
        TextLN.clear();
        TextE.clear();
        TextP.clear();
    }

    public void handleDeleteUser(){
        try{
            Long id = Long.parseLong(TextID.getText());
            try{
                Optional<User> deletedUser = serv.delete(id);
                if (deletedUser.isEmpty())
                    AdminActionAlert.showMessage(null, Alert.AlertType.ERROR, "Error", "Eroare! Nu exista User cu ID-ul dat!");
                else update(new UserTaskChangeEvent(ChangeEventType.DELETE, deletedUser.get()));
            }catch(Exception e){
                AdminActionAlert.showMessage(null, Alert.AlertType.ERROR, "Error", e.getMessage());
            }
        }catch(Exception e){
            AdminActionAlert.showMessage(null, Alert.AlertType.ERROR, "Error","ID invalid! ID-ul trebuie sa fie un numar!");
        }
    }

    public void handleUpdateUser(){
        String first_name = TextFN.getText();
        String last_name = TextLN.getText();
        String email = TextE.getText();
        try {
            Long id = Long.parseLong(TextID.getText());
            User user = new User(first_name,last_name,email,id,"");
            try {
                Optional<User> updatedUser = serv.update(user);
                if (updatedUser.isEmpty())
                    AdminActionAlert.showMessage(null, Alert.AlertType.ERROR, "Error", "Eroare! Nu exista Userul dat!");
                else update(new UserTaskChangeEvent(ChangeEventType.UPDATE, user));
            }
            catch (Exception e){
                AdminActionAlert.showMessage(null, Alert.AlertType.ERROR, "Error", e.getMessage());
            }
        }
        catch (Exception e){
            AdminActionAlert.showMessage(null, Alert.AlertType.ERROR, "Error", "ID invalid! ID-ul trebuie sa fie un numar!");
        }

        TextID.clear();
        TextFN.clear();
        TextLN.clear();
        TextE.clear();
        TextP.clear();
    }

    public void handleFindUser()  {
        try {
            Long id = Long.parseLong(TextID.getText());
            Optional<User> foundUser = serv.getEntityById(id);
            if (foundUser.isEmpty()) {
                AdminActionAlert.showMessage(null, Alert.AlertType.ERROR, "Error", "Eroare! Nu exista utilizator cu ID-ul dat!");
                TextFN.clear();
                TextLN.clear();
                TextE.clear();
            }
            else{
                TextFN.setText(foundUser.get().getFirstname());
                TextLN.setText(foundUser.get().getLastname());
                TextE.setText(foundUser.get().getEmail());
            }
        }
        catch (Exception e){
            AdminActionAlert.showMessage(null, Alert.AlertType.ERROR, "Error", "ID invalid! ID-ul trebuie sa fie un numar!");
            TextFN.clear();
            TextLN.clear();
            TextE.clear();

        }
    }

    public void handleSelectUser(){
        User utilizator = (User) tableView.getSelectionModel().getSelectedItem();

        TextID.setText(utilizator.getId().toString());
        TextFN.setText(utilizator.getFirstname());
        TextLN.setText(utilizator.getLastname());
        TextE.setText(utilizator.getEmail());
        TextP.setText(utilizator.getPassword());
    }

    public void handleExitButton(){
        Node src = ExitButton;
        Stage stage = (Stage) src.getScene().getWindow();

        stage.close();
    }
}
