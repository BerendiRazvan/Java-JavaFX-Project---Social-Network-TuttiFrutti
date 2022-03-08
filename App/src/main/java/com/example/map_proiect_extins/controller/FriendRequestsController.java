package com.example.map_proiect_extins.controller;

import com.example.map_proiect_extins.HomeWindow;
import com.example.map_proiect_extins.UserDashboard;
import com.example.map_proiect_extins.domain.FriendshipDTO;
import com.example.map_proiect_extins.domain.User;
import com.example.map_proiect_extins.service.FriendshipService;
import com.example.map_proiect_extins.service.MessageService;
import com.example.map_proiect_extins.service.UserService;
import com.example.map_proiect_extins.utils.events.FriendshipStatusEvent;
import com.example.map_proiect_extins.utils.observer.Observer;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class FriendRequestsController implements Observer<FriendshipStatusEvent> {
    private UserDashboard userDashboard = new UserDashboard();
    private Random rand = new Random();

    ObservableList<FriendshipDTO> model = FXCollections.observableArrayList();
    List<FriendshipDTO> userFriendRequests = new ArrayList<>();

    @FXML
    private TableView<FriendshipDTO> tableViewFriendRequests;
    @FXML
    public TableColumn<FriendshipDTO, LocalDate> tableColumnDate;
    @FXML
    public TableColumn<FriendshipDTO, String> tableColumnStatus;
    @FXML
    private TableColumn<FriendshipDTO, String> tableColumnFriendLastName;
    @FXML
    private TableColumn<FriendshipDTO, String> tableColumnFriendFirstName;
    @FXML
    public TableColumn<ImageView,ImageView> imagineColumn;

    @FXML
    private void initialize() {

        tableColumnFriendFirstName.setCellValueFactory(new PropertyValueFactory<FriendshipDTO, String>("friendFirstName"));
        tableColumnFriendLastName.setCellValueFactory(new PropertyValueFactory<FriendshipDTO, String>("friendLastName"));
        tableColumnDate.setCellValueFactory(new PropertyValueFactory<FriendshipDTO, LocalDate>("date"));
        tableColumnStatus.setCellValueFactory(new PropertyValueFactory<FriendshipDTO, String>("status"));
        tableViewFriendRequests.setItems(model);

    }

    public void setServices(UserService userService, FriendshipService friendshipService, User currentUser, MessageService msgService) {
        userDashboard.setUserService(userService);
        userDashboard.setFriendshipService(friendshipService);
        userDashboard.setCurrentUser(currentUser);
        userDashboard.setMessageService(msgService);
        initModel();
        userDashboard.friendshipServiceAddObserver(this);

        setProfilePhotos();
    }

    private void setProfilePhotos(){
        imagineColumn.setCellValueFactory(c->
                {
                    int rand_int = rand.nextInt(41);
                    String photoPath = "images/user"+rand_int+".png";

                    ImageView imageView = new ImageView();
                    imageView.setImage(new Image(HomeWindow.class.getResource(photoPath).toExternalForm()));
                    imageView.setFitHeight(40);
                    imageView.setFitWidth(40);

                    return new SimpleObjectProperty<>(imageView);
                }
        );
    }

    private void initModel() {
        userFriendRequests = userDashboard.findRequestsDTO();
        model.setAll(userFriendRequests);
    }

    @FXML
    public void handleAccept(ActionEvent actionEvent) {
        FriendshipDTO friendshipSelected = tableViewFriendRequests.getSelectionModel().getSelectedItem();
        try {
            if (friendshipSelected != null) {
                userDashboard.updateRequest(friendshipSelected);
                userFriendRequests.remove(friendshipSelected);
                model.setAll(userFriendRequests);
                MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Friend request", "Request accepted");
            } else {
                MessageAlert.showErrorMessage(null, "You have not selected any friendship");
            }

        } catch (Exception exception) {
            MessageAlert.showErrorMessage(null, exception.getMessage());
        }

    }

    @FXML
    public void handleReject(ActionEvent actionEvent) {
        FriendshipDTO friendshipSelected = tableViewFriendRequests.getSelectionModel().getSelectedItem();
        try {
            if (friendshipSelected != null) {
                userDashboard.updateRequest(friendshipSelected);

                userFriendRequests.remove(friendshipSelected);
                model.setAll(userFriendRequests);
                MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Friend request", "Request denied");
            } else {
                MessageAlert.showErrorMessage(null, "You have not selected any friendship");
            }

        } catch (Exception exception) {
            MessageAlert.showErrorMessage(null, exception.getMessage());
        }
    }


    @Override
    public void update(FriendshipStatusEvent statusEvent) {
        initModel();
    }
}
