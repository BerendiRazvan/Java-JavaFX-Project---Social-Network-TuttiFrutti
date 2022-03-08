package com.example.map_proiect_extins.controller;


import com.example.map_proiect_extins.HomeWindow;
import com.example.map_proiect_extins.UserDashboard;
import com.example.map_proiect_extins.domain.User;
import com.example.map_proiect_extins.service.FriendshipService;
import com.example.map_proiect_extins.service.MessageService;
import com.example.map_proiect_extins.service.UserService;
import com.example.map_proiect_extins.utils.events.UserStatusEvent;
import com.example.map_proiect_extins.utils.observer.Observer;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;



public class RequestFriendshipController implements Observer<UserStatusEvent> {
    UserDashboard userDashboard =new UserDashboard();
    private Random rand = new Random();

    ObservableList<User> model = FXCollections.observableArrayList();
    List<User> usersNoFriends = new ArrayList<>();

    @FXML
    private TableColumn<User, String> tableColumnLastName;
    @FXML
    private TableColumn<User, String> tableColumnFirstName;
    @FXML
    private TableView<User> tableViewUsers;
    @FXML
    public TextField textFieldSearch;
    @FXML
    public Button backBtn;
    @FXML
    public Button previousBtn;
    @FXML
    public Button nextBtn;
    @FXML
    public TableColumn<ImageView,ImageView> imagineColumn;

    @FXML
    private void initialize() {

        tableColumnFirstName.setCellValueFactory(new PropertyValueFactory<User, String>("firstName"));
        tableColumnLastName.setCellValueFactory(new PropertyValueFactory<User, String>("lastName"));
        tableViewUsers.setItems(model);

        textFieldSearch.textProperty().addListener(o -> handleSearch());
    }

    public void setServices(UserService userService, FriendshipService friendshipService, User currentUser, MessageService msgService) {
        userDashboard.setUserService(userService);
        userDashboard.setFriendshipService(friendshipService);
        userDashboard.setCurrentUser(currentUser);
        userDashboard.setMessageService(msgService);
        userDashboard.setUserServicePage(0);
        initModel();
        userDashboard.userServiceAddObserver(this);

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

    private void updateCurrentListOfUsers(Set<User> users) {
        usersNoFriends = new ArrayList<>(users);
    }

    private void updateModel() {
        model.setAll(usersNoFriends);
    }


    private void initModel() {
        updateCurrentListOfUsers(userDashboard.getUsersOnPage());
        Set<User> users = userDashboard.existNextPageOfUsers();
        if(users==null && userDashboard.getUserServicePage()==0)
            nextBtn.setVisible(false);
        updateModel();
    }

    private void handleSearch() {
        Predicate<User> userPredicate = user -> user.getFirstName().startsWith(textFieldSearch.getText());
        model.setAll(usersNoFriends
                .stream()
                .filter(userPredicate)
                .collect(Collectors.toList()));
    }

    @FXML
    public void handleAddFriendRequest(ActionEvent actionEvent) {
        User userSelected = tableViewUsers.getSelectionModel().getSelectedItem();
        try {
            if (userSelected != null) {
                userDashboard.addFriend(userSelected.getId());

                usersNoFriends.remove(userSelected);
                updateModel();
                MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Friend request", "Friend request was sent");
            } else {
                MessageAlert.showErrorMessage(null, "You have not selected any users");
            }
        } catch (Exception exception) {
            MessageAlert.showErrorMessage(null, exception.getMessage());

        }
    }


    @FXML
    public void handlePrevious(ActionEvent actionEvent) {
        Set<User> users = userDashboard.getPreviousPageOfUsers();
        if (users != null && userDashboard.getUserServicePage() == 0) {
            updateCurrentListOfUsers(users);
            updateModel();
            previousBtn.setVisible(false);
            nextBtn.setVisible(true);
        } else {
            if (users != null) {
                updateCurrentListOfUsers(users);
                updateModel();
                nextBtn.setVisible(true);
            }
        }
    }


    @FXML
    public void handleNext(ActionEvent actionEvent) {
        Set<User> users = userDashboard.getNextPageOfUsers();
        int checkNextUsers = userDashboard.checkNextUsers();
        if (checkNextUsers == -1) {
            nextBtn.setVisible(false);
        }
        if (users != null)
            if (users.size() < userDashboard.getUserServiceSize()) {
                updateCurrentListOfUsers(users);
                updateModel();
                nextBtn.setVisible(false);
                previousBtn.setVisible(true);
            } else {
                updateCurrentListOfUsers(users);
                updateModel();
                previousBtn.setVisible(true);
            }
    }

    @Override
    public void update(UserStatusEvent statusEvent) {
        initModel();
    }
}
