package com.example.map_proiect_extins.controller;


import com.example.map_proiect_extins.UserDashboard;
import com.example.map_proiect_extins.domain.User;
import com.example.map_proiect_extins.service.EventService;
import com.example.map_proiect_extins.service.UserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;



public class NewEventController {

    List<String> hours = new ArrayList<>();
    List<String> minutes = new ArrayList<>();
    private UserDashboard userDashboard = new UserDashboard();

    @FXML
    public Button saveBtn;


    @FXML
    public TextField nameTextField;

    @FXML
    public TextArea descriptionTextField;

    @FXML
    public DatePicker startPicker;

    @FXML
    public DatePicker finishPicker;

    @FXML
    public ComboBox<String> startH;

    @FXML
    public ComboBox<String> startM;

    @FXML
    public ComboBox<String> finishH;

    @FXML
    public ComboBox<String> finishM;


    @FXML
    public Label nameError;

    @FXML
    public Label descriptionError;

    @FXML
    public Label startError;

    @FXML
    public Label finishError;

    @FXML
    public void initialize() {
        clearAllFields();

        nameTextField.textProperty().addListener(o -> nameError.setText(""));
        descriptionTextField.textProperty().addListener(o -> descriptionError.setText(""));

        startPicker.valueProperty().addListener(o -> startError.setText(""));
        startH.valueProperty().addListener(o -> startError.setText(""));
        startM.valueProperty().addListener(o -> startError.setText(""));

        finishPicker.valueProperty().addListener(o -> finishError.setText(""));
        finishM.valueProperty().addListener(o -> finishError.setText(""));
        finishH.valueProperty().addListener(o -> finishError.setText(""));
    }

    private void clearAllFields(){
        nameError.setText("");
        descriptionError.setText("");
        startError.setText("");
        finishError.setText("");

        nameTextField.setText("");
        descriptionTextField.setText("");

        startPicker.valueProperty().set(null);
        startH.getSelectionModel().selectFirst();
        startM.getSelectionModel().selectFirst();

        finishPicker.valueProperty().set(null);
        finishM.getSelectionModel().selectFirst();
        finishH.getSelectionModel().selectFirst();


        nameTextField.setPromptText("Event name");
        descriptionTextField.setPromptText("Someting about your event");

        startPicker.setPromptText("Start date");
        startH.setPromptText("Hour");
        startM.setPromptText("Minute");

        finishPicker.setPromptText("Finish date");
        finishM.setPromptText("Hour");
        finishH.setPromptText("Minute");

    }

    public void setServices(UserService userService, EventService eventService, User currentUser) {
        userDashboard.setEventService(eventService);
        userDashboard.setUserService(userService);
        userDashboard.setCurrentUser(currentUser);

        hours.add("Hour");
        minutes.add("Minute");
        for (int h = 0; h < 24; h++)
            hours.add(String.valueOf(h));
        for (int m = 0; m < 60; m++)
            minutes.add(String.valueOf(m));

        startH.getItems().setAll(hours);
        startM.getItems().setAll(minutes);
        finishH.getItems().setAll(hours);
        finishM.getItems().setAll(minutes);
    }

    @FXML
    public void onActionSave(ActionEvent event) {
        Boolean save = true;

        if (nameTextField.getText().matches("")) {
            save = false;
            nameError.setText("Please enter a name for event!");
        }

        if (descriptionTextField.getText().matches("")) {
            save = false;
            descriptionError.setText("Please enter a description for event!");
        }

        if (startPicker.getValue() == null) {
            save = false;
            startError.setText("Incomplect start date&time!");
        }

        if (startM.getValue() == null || startM.getValue().matches("Minute")) {
            save = false;
            startError.setText("Incomplect start date&time!");
        }

        if (startH.getValue() == null || startH.getValue().matches("Hour")) {
            save = false;
            startError.setText("Incomplect start date&time!");
        }

        if (startPicker.getValue() == null && (startM.getValue() == null || startM.getValue().matches("Minute")) && (startH.getValue() == null || startH.getValue().matches("Hour"))) {
            save = false;
            startError.setText("Please select start date&time!");
        }

        if (finishPicker.getValue() == null) {
            save = false;
            finishError.setText("Incomplect finish date&time!");
        }

        if (finishM.getValue() == null || finishM.getValue().matches("Minute")) {
            save = false;
            finishError.setText("Incomplect finish date&time!");
        }

        if (finishH.getValue() == null || finishH.getValue().matches("Hour")) {
            save = false;
            finishError.setText("Incomplect finish date&time!");
        }

        if (finishPicker.getValue() == null && finishM.getValue() == null && finishH.getValue() == null) {
            save = false;
            finishError.setText("Please select finish date&time!");
        }

        if (save == true) {
            try {
                LocalTime startTime = LocalTime.of(Integer.parseInt(startH.getValue()), Integer.parseInt(startM.getValue()), 0);
                LocalDateTime startEvent = LocalDateTime.of(startPicker.getValue(), startTime);

                LocalTime finishTime = LocalTime.of(Integer.parseInt(finishH.getValue()), Integer.parseInt(finishM.getValue()), 0);
                LocalDateTime finishEvent = LocalDateTime.of(finishPicker.getValue(), finishTime);

                userDashboard.saveEvent(nameTextField.getText(), descriptionTextField.getText(), startEvent, finishEvent, userDashboard.getCurrentUser());

                Alert message = new Alert(Alert.AlertType.INFORMATION);
                message.setTitle("New Event");
                message.setContentText("Successful save");
                message.showAndWait();

                clearAllFields();
            } catch (Exception e) {
                String[] error =  e.getMessage().split("\n");
                startError.setText(error[0]);
                finishError.setText(error[1]);
            }
        }
    }


}
