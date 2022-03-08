package com.example.map_proiect_extins.controller;

import com.example.map_proiect_extins.UserDashboard;
import com.example.map_proiect_extins.domain.UserFriendsDTO;
import com.example.map_proiect_extins.domain.User;
import com.example.map_proiect_extins.service.FriendshipService;
import com.example.map_proiect_extins.service.MessageService;
import com.example.map_proiect_extins.service.UserService;
import com.example.map_proiect_extins.utils.events.FriendshipStatusEvent;
import com.example.map_proiect_extins.utils.observer.Observer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.ArrayList;
import java.util.List;



public class FrienshipsController implements Observer<FriendshipStatusEvent> {
    private UserDashboard userDashboard = new UserDashboard();

    ObservableList<UserFriendsDTO> modelTable = FXCollections.observableArrayList();
    List<UserFriendsDTO> userFriends = new ArrayList<>();


    @FXML
    TableColumn<UserFriendsDTO, String> firstNameUsr;

    @FXML
    TableColumn<UserFriendsDTO, String> lastNameUsr;

    @FXML
    TableView<UserFriendsDTO> tableUserFriendships;


    @FXML
    Button deleteFriendBtn;
    @FXML
    public Button nextBtn;
    @FXML
    public Button previousBtn;

    @FXML
    public void initialize() {
        firstNameUsr.setCellValueFactory(new PropertyValueFactory<UserFriendsDTO, String>("firstName"));
        lastNameUsr.setCellValueFactory(new PropertyValueFactory<UserFriendsDTO, String>("lastName"));

        tableUserFriendships.setItems(modelTable);

    }

    public void setService(FriendshipService frService, UserService usrService, User loggedUser, MessageService msgServive) {
        userDashboard.setFriendshipService(frService);
        userDashboard.setCurrentUser(loggedUser);
        userDashboard.setUserService(usrService);
        userDashboard.setMessageService(msgServive);
        userDashboard.setFriendshipServicePage(0);
        initModel();
        userDashboard.friendshipServiceAddObserver(this);
    }


    private void updateCurrentListOfFriends(List<UserFriendsDTO> friends) {
        userFriends = new ArrayList<>(friends);
    }

    private void updateModel() {
        modelTable.setAll(userFriends);
    }

    private void initModel() {
        updateCurrentListOfFriends(userDashboard.getFriendsOnPage());
        List<UserFriendsDTO> userFriends = userDashboard.existsNextPageOfFriends();
        if (userFriends == null && userDashboard.getFriendshipServicePage() == 0)
            nextBtn.setVisible(false);
        updateModel();
    }


    @FXML
    public void onActionDeleteFriendBtn() {

        UserFriendsDTO friendship = tableUserFriendships.getSelectionModel().getSelectedItem();

        if (friendship == null) {
            Alert message = new Alert(Alert.AlertType.INFORMATION);
            message.setTitle("INFO App");
            message.setContentText("Select a friend");
            message.showAndWait();
        } else {
            try {
                userDashboard.removeFriendUserFriendsDTO(friendship);

                Alert message = new Alert(Alert.AlertType.INFORMATION);
                message.setTitle("Delete");
                message.setContentText("Successful deletion");
                message.showAndWait();
                userFriends.remove(friendship);
                updateModel();
            } catch (Exception e) {
                Alert errorMessage = new Alert(Alert.AlertType.ERROR);
                errorMessage.setTitle("Delete error");
                errorMessage.setContentText(e.getMessage());
                errorMessage.showAndWait();
            }
        }
    }

    @FXML
    public void handleNext(ActionEvent actionEvent) {
        List<UserFriendsDTO> userFriends = userDashboard.getNextPageOfFriends();
        int checkNextFriends = userDashboard.checkNextFriends();
        if (checkNextFriends == -1 || userFriends == null) {
            nextBtn.setVisible(false);
        }
        if (userFriends != null)
            if (userFriends.size() < userDashboard.getFriendshipServiceSize()) {
                updateCurrentListOfFriends(userFriends);
                updateModel();
                nextBtn.setVisible(false);
                previousBtn.setVisible(true);
            } else {
                updateCurrentListOfFriends(userFriends);
                updateModel();
                previousBtn.setVisible(true);
            }
    }

    @FXML
    public void handlePrevious(ActionEvent actionEvent) {
        List<UserFriendsDTO> userFriends = userDashboard.getPreviousPageOfFriends();
        if (userFriends != null && userDashboard.getFriendshipServicePage() == 0) {
            updateCurrentListOfFriends(userFriends);
            updateModel();
            previousBtn.setVisible(false);
            nextBtn.setVisible(true);
        } else {
            if (userFriends != null) {
                updateCurrentListOfFriends(userFriends);
                updateModel();
                nextBtn.setVisible(true);
            }
        }
    }

    @Override
    public void update(FriendshipStatusEvent statusEvent) {
        initModel();
    }
}
