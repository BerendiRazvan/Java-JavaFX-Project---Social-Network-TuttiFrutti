package com.example.map_proiect_extins.controller;


import com.example.map_proiect_extins.LogInWindow;
import com.example.map_proiect_extins.UserDashboard;
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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;


import java.util.Locale;

public class SignUpController {
    private UserDashboard userDashboard = new UserDashboard();

    @FXML
    Label firstNameError;
    @FXML
    Label lastNameError;
    @FXML
    Label emailError;
    @FXML
    Label passwordError;
    @FXML
    Label passwordVerifError;

    @FXML
    Button createAccountBtn;

    @FXML
    TextField firstNameTextField;
    @FXML
    TextField lastNameTextField;
    @FXML
    TextField emailTextField;
    @FXML
    TextField passwordTextField;
    @FXML
    TextField passwordVerifTextField;

    @FXML
    public void initialize() {
        clearFields();
        clearErrors();

        firstNameTextField.textProperty().addListener(o -> firstNameError.setText(""));
        lastNameTextField.textProperty().addListener(o -> lastNameError.setText(""));
        emailTextField.textProperty().addListener(o -> emailError.setText(""));
        passwordTextField.textProperty().addListener(o -> passwordError.setText(""));
        passwordVerifTextField.textProperty().addListener(o -> passwordVerifError.setText(""));

        textFieldsListener();
    }
    private void textFieldsListener(){
        firstNameError.textProperty().addListener(o -> {
            if(firstNameError.getText().matches(""))
                firstNameTextField.setStyle(normalStyle());
            else
                firstNameTextField.setStyle(errorStyle());
        });

        lastNameError.textProperty().addListener(o -> {
            if(lastNameError.getText().matches(""))
                lastNameTextField.setStyle(normalStyle());
            else
                lastNameTextField.setStyle(errorStyle());
        });

        emailError.textProperty().addListener(o -> {
            if(emailError.getText().matches(""))
                emailTextField.setStyle(normalStyle());
            else
                emailTextField.setStyle(errorStyle());
        });

        passwordError.textProperty().addListener(o -> {
            if(passwordError.getText().matches(""))
                passwordTextField.setStyle(normalStyle());
            else
                passwordTextField.setStyle(errorStyle());
        });

        passwordVerifError.textProperty().addListener(o -> {
            if(passwordVerifError.getText().matches(""))
                passwordVerifTextField.setStyle(normalStyle());
            else
                passwordVerifTextField.setStyle(errorStyle());
        });
    }

    private String errorStyle() {
        return "-fx-border-color:  #C23838;" +
                "-fx-text-fill: #2b305e;" +
                "-fx-background-color: transparent;"+
                "-fx-border-color: #C23838;" +
                "-fx-border-width: 0px 0px 2px 0px;";
    }

    private String normalStyle() {
        return "-fx-border-color:  #2b305e;" +
                "-fx-text-fill: #2b305e;" +
                "-fx-background-color: transparent;"+
                "-fx-border-color:  #2b305e;" +
                "-fx-border-width: 0px 0px 2px 0px;";
    }

    private void clearFields() {
        firstNameTextField.setText("");
        lastNameTextField.setText("");
        emailTextField.setText("");
        passwordTextField.setText("");
        passwordVerifTextField.setText("");
    }

    private void clearErrors() {
        firstNameError.setText("");
        lastNameError.setText("");
        emailError.setText("");
        passwordError.setText("");
        passwordVerifError.setText("");
    }

    @FXML
    public void onActionBackBtn(ActionEvent event) {
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

    public void setService(UserService usrService, FriendshipService frService, MessageService msgService, EventService eventService) {
        userDashboard.setUserService(usrService);
        userDashboard.setFriendshipService(frService);
        userDashboard.setMessageService(msgService);
        userDashboard.setEventService(eventService);
    }

    @FXML
    public void onAddAccountBtn(ActionEvent event) {

        try {
            if (passwordVerifTextField.getText().matches(passwordTextField.getText())) {
                userDashboard.saveAUser(firstNameTextField,lastNameTextField,emailTextField,passwordTextField,passwordVerifTextField);
                clearFields();
                clearErrors();

                onActionBackBtn(event);

                Alert message=new Alert(Alert.AlertType.INFORMATION);
                message.setTitle("Add Account");
                message.setContentText("Account created successfully :)");
                message.showAndWait();
            } else {
                passwordVerifError.setText("Passwords do not match! Please re-enter the password above.");
            }
        } catch (Exception e) {
            String[] errors;
            errors = e.getMessage().split("\n");

            for (String err : errors) {
                System.out.println(err);
                if (err.toUpperCase(Locale.ROOT).contains("FIRST")) {
                    firstNameError.setText(err);
                }
                if (err.toUpperCase(Locale.ROOT).contains("LAST")) {
                    lastNameError.setText(err);
                }
                if (err.toUpperCase(Locale.ROOT).contains("EMAIL")) {
                    emailError.setText(err);
                }
                if (err.toUpperCase(Locale.ROOT).contains("PASSWORD")) {
                    passwordError.setText(err);
                }

            }
        }


    }


}
