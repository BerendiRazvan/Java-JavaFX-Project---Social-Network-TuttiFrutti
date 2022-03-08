package com.example.map_proiect_extins.controller;

import com.example.map_proiect_extins.HomeWindow;
import com.example.map_proiect_extins.LogInWindow;
import com.example.map_proiect_extins.UserDashboard;
import com.example.map_proiect_extins.domain.User;
import com.example.map_proiect_extins.service.EventService;
import com.example.map_proiect_extins.domain.UserFriendsDTO;
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
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class HomeController implements Observer<FriendshipStatusEvent> {
    UserDashboard userDashboard = new UserDashboard();

    ObservableList<UserFriendsDTO> modelTable = FXCollections.observableArrayList();
    List<UserFriendsDTO> userFriends = new ArrayList<>();


    @FXML
    TableColumn<UserFriendsDTO, String> firstNameUsr;

    @FXML
    TableColumn<UserFriendsDTO, String> lastNameUsr;

    @FXML
    TableView<UserFriendsDTO> tableUserFriendships;

    @FXML
    TableColumn<ImageView,ImageView> imagineColumn;

    private Random rand = new Random();

    @FXML
    Button deleteFriendBtn;
    @FXML
    public Button nextBtn;
    @FXML
    public Button previousBtn;

    @FXML
    Label allNameLabel;

    @FXML
    public Button friendshipsBtn;
    @FXML
    public Button addFriend;
    @FXML
    public Button friendRequestsBtn;
    @FXML
    public Button myRequestsBtn;
    @FXML
    public Button conversationsBtn;
    @FXML
    public Button reportsBtn;
    @FXML
    public Button eventsBtn;
    @FXML
    private BorderPane borderPane;
    @FXML
    private AnchorPane anchorPane;

    @FXML
    private ImageView profileImage;

    @FXML
    public void initialize() {
        firstNameUsr.setCellValueFactory(new PropertyValueFactory<UserFriendsDTO, String>("firstName"));
        lastNameUsr.setCellValueFactory(new PropertyValueFactory<UserFriendsDTO, String>("lastName"));
        tableUserFriendships.setItems(modelTable);
    }

    public void  clearSelected(){
        friendshipsBtn.setStyle("-fx-background-color: #2b305e;");
        addFriend.setStyle("-fx-background-color: #2b305e;");
        friendRequestsBtn.setStyle("-fx-background-color: #2b305e;");
        myRequestsBtn.setStyle("-fx-background-color: #2b305e;");
        conversationsBtn.setStyle("-fx-background-color: #2b305e;");
        reportsBtn.setStyle("-fx-background-color: #2b305e;");
        eventsBtn.setStyle("-fx-background-color: #2b305e;");

    }

    public void setService(FriendshipService frService, UserService usrService, User loggedUser, MessageService msgServive, EventService eventService) {
        userDashboard.setFriendshipService(frService);
        userDashboard.setCurrentUser(loggedUser);
        userDashboard.setUserService(usrService);
        userDashboard.setMessageService(msgServive);
        userDashboard.setEventService(eventService);

        allNameLabel.setText(userDashboard.getCurrentUserFirstName() + " " + userDashboard.getCurrentUserLastName());
        friendshipsBtn.setStyle("-fx-background-color: #374785;");

        userDashboard.setFriendshipServicePage(0);
        initModel();
        userDashboard.friendshipServiceAddObserver(this);

        setProfilePhoto();
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
    private void setProfilePhoto(){
        int rand_int = rand.nextInt(41);
        String photoPath = "images/user"+rand_int+".png";

        profileImage.setImage(new Image(HomeWindow.class.getResource(photoPath).toExternalForm()));
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

    @FXML
    public void onMouseFriendships(MouseEvent mouseEvent) {
        clearSelected();
        friendshipsBtn.setStyle("-fx-background-color: #374785;");
        borderPane.setCenter(anchorPane);
    }


    @FXML
    public void onMouseAddFriend(MouseEvent mouseEvent) {
        clearSelected();
        addFriend.setStyle("-fx-background-color: #374785;");
        Parent root = null;
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(HomeWindow.class.getResource("views/requestFriendship-view.fxml"));
            root = loader.load();
            RequestFriendshipController controller = loader.getController();
            controller.setServices(userDashboard.getUserService(), userDashboard.getFriendshipService(), userDashboard.getCurrentUser(), userDashboard.getMessageService());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        borderPane.setCenter(root);
    }

    @FXML
    public void onMouseFriendRequests(MouseEvent mouseEvent) {
        clearSelected();
        friendRequestsBtn.setStyle("-fx-background-color: #374785;");
        Parent root =null;
        try{
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(HomeWindow.class.getResource("views/friendRequests-view.fxml"));
            root =loader.load();
            FriendRequestsController controller = loader.getController();
            controller.setServices(userDashboard.getUserService(), userDashboard.getFriendshipService(), userDashboard.getCurrentUser(), userDashboard.getMessageService());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        borderPane.setCenter(root);

    }

    @FXML
    public void onMouseMyRequests(MouseEvent mouseEvent) {
        clearSelected();
        myRequestsBtn.setStyle("-fx-background-color: #374785;");
        Parent root =null;
        try{
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(HomeWindow.class.getResource("views/currentUserRequests-view.fxml"));
            root =loader.load();
            CurrentUserRequestsController controller = loader.getController();
            controller.setServices(userDashboard.getUserService(), userDashboard.getFriendshipService(), userDashboard.getCurrentUser(), userDashboard.getMessageService());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        borderPane.setCenter(root);
    }


    @FXML
    public void onMouseConversations(MouseEvent mouseEvent) {
        clearSelected();
        conversationsBtn.setStyle("-fx-background-color: #374785;");
        Parent root =null;
        try{
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(HomeWindow.class.getResource("views/messages-view.fxml"));
            root =loader.load();
            MessagesController controller = loader.getController();
            controller.setServices(userDashboard.getUserService(), userDashboard.getFriendshipService(), userDashboard.getCurrentUser(), userDashboard.getMessageService());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        borderPane.setCenter(root);
    }

    @FXML
    public void onMouseReports(MouseEvent mouseEvent) {
        clearSelected();
        reportsBtn.setStyle("-fx-background-color: #374785;");
        Parent root =null;
        try{
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(HomeWindow.class.getResource("views/raports-view.fxml"));
            root =loader.load();
            RaportsController controller = loader.getController();
            controller.setServices(userDashboard.getUserService(), userDashboard.getFriendshipService(), userDashboard.getCurrentUser(), userDashboard.getMessageService());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        borderPane.setCenter(root);

    }


    @FXML
    public void onMouseEvents(MouseEvent mouseEvent) {
        clearSelected();
        eventsBtn.setStyle("-fx-background-color: #374785;");
        Parent root =null;
        try{
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(HomeWindow.class.getResource("views/eventsAndNotif-view.fxml"));
            root =loader.load();
            EventsAndNotifController controller = loader.getController();
            controller.setServices(userDashboard.getUserService(), userDashboard.getEventService(), userDashboard.getCurrentUser(), userDashboard.getFriendshipService(), userDashboard.getMessageService());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        borderPane.setCenter(root);
    }

    @FXML
    public void onActionLogOutBtn(ActionEvent event) {
        try {
            Stage stage;
            Scene scene;

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(LogInWindow.class.getResource("views/login-view.fxml"));
            BorderPane root = loader.load();

            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            LoginController controller = loader.getController();
            controller.setService(userDashboard.getUserService(), userDashboard.getFriendshipService(), userDashboard.getMessageService(), userDashboard.getEventService());
            stage.setScene(scene);
            stage.setMinWidth(750);
            stage.setMinHeight(500);
            stage.show();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}
