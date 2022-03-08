package com.example.map_proiect_extins.controller;

import com.example.map_proiect_extins.FriendshipsWindow;
import com.example.map_proiect_extins.HomeWindow;
import com.example.map_proiect_extins.UserDashboard;
import com.example.map_proiect_extins.domain.User;
import com.example.map_proiect_extins.service.EventService;
import com.example.map_proiect_extins.service.FriendshipService;
import com.example.map_proiect_extins.service.MessageService;
import com.example.map_proiect_extins.service.UserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import java.util.Locale;


public class LoginController {

    private UserDashboard userDashboard = new UserDashboard();

    @FXML
    Label mailError;

    @FXML
    Label passwordError;

    @FXML
    TextField emailTextField;

    @FXML
    PasswordField passwordTextField;


    @FXML
    public void initialize() {
        clearFields();
        clearErrors();

        emailTextField.textProperty().addListener(o -> mailError.setText(""));
        passwordTextField.textProperty().addListener(o -> passwordError.setText(""));

        textFieldsListener();
    }

    private void textFieldsListener() {
        mailError.textProperty().addListener(o -> {
            if (mailError.getText().matches(""))
                emailTextField.setStyle(normalStyle());
            else
                emailTextField.setStyle(errorStyle());
        });

        passwordError.textProperty().addListener(o -> {
            if (passwordError.getText().matches(""))
                passwordTextField.setStyle(normalStyle());
            else
                passwordTextField.setStyle(errorStyle());
        });
    }

    private String errorStyle() {
        return "-fx-background-color:  #FFE6E6;" +
                "-fx-border-color:  #C23838;" +
                "-fx-text-fill: #2b305e;" +
                "-fx-background-color: transparent;"+
                "-fx-border-color: #C23838;" +
                "-fx-border-width: 0px 0px 2px 0px;";
    }

    private String normalStyle() {
        return "-fx-background-color:  #e3eff6;" +
                "-fx-border-color:  #2b305e;" +
                "-fx-text-fill: #2b305e;" +
                "-fx-background-color: transparent;"+
                "-fx-border-color:  #2b305e;" +
                "-fx-border-width: 0px 0px 2px 0px;";
    }


    public void setService(UserService usrService, FriendshipService frService, MessageService msgService, EventService eventService) {
        userDashboard.setUserService(usrService);
        userDashboard.setFriendshipService(frService);
        userDashboard.setMessageService(msgService);
        userDashboard.setEventService(eventService);
    }


    @FXML
    protected void onActionLogInBtn(ActionEvent event) {
        Boolean ok = true;
        if (emailTextField.getText().matches("")) {
            mailError.setText("Please entre an email!");
            ok = false;
        }

        if (passwordTextField.getText().matches("")) {
            passwordError.setText("Please entre a password!");
            ok = false;
        }

        if (ok == true) {
            try {
                User loggedInUser = userDashboard.logInUser(emailTextField, passwordTextField);

                clearFields();
                openNextWindow(event, loggedInUser);
            } catch (Exception e) {
                if (e.getMessage().toUpperCase(Locale.ROOT).contains("PASSWORD"))
                    passwordError.setText(e.getMessage());
                else
                    mailError.setText(e.getMessage());
            }
        }
    }

    private void clearFields() {
        emailTextField.setText("");
        passwordTextField.setText("");

        clearErrors();
    }

    private void clearErrors() {
        mailError.setText("");
        passwordError.setText("");
    }


    public void openNextWindow(ActionEvent event, User loggedUser) {
        try {
            Stage stage;
            Scene scene;

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(HomeWindow.class.getResource("views/home-view.fxml"));
            BorderPane root = loader.load();

            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            HomeController controller = loader.getController();
            controller.setService(userDashboard.getFriendshipService(), userDashboard.getUserService(), loggedUser, userDashboard.getMessageService(), userDashboard.getEventService());
            stage.setScene(scene);
            stage.setMinWidth(750);
            stage.setMinHeight(500);
            stage.show();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    public void onActionSignUpBtn(ActionEvent event) {
        try {
            Stage stage;
            Scene scene;

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(FriendshipsWindow.class.getResource("views/signup-view.fxml"));
            AnchorPane root = loader.load();

            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            SignUpController controller = loader.getController();
            controller.setService(userDashboard.getUserService(), userDashboard.getFriendshipService(), userDashboard.getMessageService(), userDashboard.getEventService());
            stage.setScene(scene);
            stage.setMinWidth(750);
            stage.setMinHeight(500);
            stage.show();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    public void onActionForgotPasswordBtn() {
        Alert message = new Alert(Alert.AlertType.INFORMATION);
        message.setTitle("INFO App");
        message.setContentText("We are working on it. Button will be ready to use soon.");
        message.showAndWait();
    }

}
