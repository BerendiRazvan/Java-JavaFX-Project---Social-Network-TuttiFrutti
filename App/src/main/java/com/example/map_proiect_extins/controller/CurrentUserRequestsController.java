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
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class CurrentUserRequestsController implements Observer<FriendshipStatusEvent> {
    private UserDashboard userDashboard = new UserDashboard();
    private Random rand = new Random();

    ObservableList<FriendshipDTO> model = FXCollections.observableArrayList();
    List<FriendshipDTO> currentUserRequests = new ArrayList<>();

    @FXML
    private TableView<FriendshipDTO> tableViewCurrentUserRequests;
    @FXML
    public TableColumn<FriendshipDTO, LocalDate> tableColumnDate;
    @FXML
    public TableColumn<FriendshipDTO, String> tableColumnStatus;
    @FXML
    private TableColumn<FriendshipDTO, String> tableColumnLastName;
    @FXML
    private TableColumn<FriendshipDTO, String> tableColumnFirstName;
    @FXML
    Button cancelRequestBtn;
    @FXML
    Button backBtn;
    @FXML
    TableColumn<ImageView,ImageView> imagineColumn;

    @FXML
    private void initialize() {
        tableColumnFirstName.setCellValueFactory(new PropertyValueFactory<FriendshipDTO, String>("friendFirstName"));
        tableColumnLastName.setCellValueFactory(new PropertyValueFactory<FriendshipDTO, String>("friendLastName"));
        tableColumnDate.setCellValueFactory(new PropertyValueFactory<FriendshipDTO, LocalDate>("date"));
        tableColumnStatus.setCellValueFactory(new PropertyValueFactory<FriendshipDTO, String>("status"));
        tableViewCurrentUserRequests.setItems(model);

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
        currentUserRequests = userDashboard.findCurrentUserRequests();
        model.setAll(currentUserRequests);
    }


    @FXML
    public void handleCancelRequest(ActionEvent actionEvent) {
        FriendshipDTO userSelected = tableViewCurrentUserRequests.getSelectionModel().getSelectedItem();
        try {
            if (userSelected != null) {
                userDashboard.removeFriendFriendshipDto(userSelected);
                currentUserRequests.remove(userSelected);
                model.setAll(currentUserRequests);
                MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Friend request", "Cancel request");
            } else {
                MessageAlert.showErrorMessage(null, "You have not selected any requests");
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
