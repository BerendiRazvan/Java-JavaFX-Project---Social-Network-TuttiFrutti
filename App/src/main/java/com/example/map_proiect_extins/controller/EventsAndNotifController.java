package com.example.map_proiect_extins.controller;


import com.example.map_proiect_extins.HomeWindow;
import com.example.map_proiect_extins.UserDashboard;
import com.example.map_proiect_extins.domain.*;
import com.example.map_proiect_extins.service.EventService;
import com.example.map_proiect_extins.service.FriendshipService;
import com.example.map_proiect_extins.service.MessageService;
import com.example.map_proiect_extins.service.UserService;
import com.example.map_proiect_extins.utils.events.EventsStatusEvent;
import com.example.map_proiect_extins.utils.observer.Observer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;



public class EventsAndNotifController implements Observer<EventsStatusEvent> {

    private UserDashboard userDashboard = new UserDashboard();
    private List<String> comboOpt = new ArrayList<>();

    ObservableList<NotifDTO> modelNotif =  FXCollections.observableArrayList();
    ObservableList<EventDTO> modelEvents = FXCollections.observableArrayList();

    @FXML
    public Button createEventBtn;

    @FXML
    public Button notifBtn;

    @FXML
    public Button statusBtn;

    @FXML
    public ComboBox<String> statusComboBox;

    @FXML
    private ListView<NotifDTO> notifList;

    @FXML
    public TableView<EventDTO> eventsTable;

    @FXML
    public TableColumn<EventDTO, String> eventColumn;

    @FXML
    public TableColumn<EventDTO, String> statusColumn;

    @FXML
    public TableColumn<EventDTO, String> notificationColumn;

    @FXML
    public void initialize() {
        notifBtn.setText("Notifications");
        statusBtn.setText("Status");

        statusBtn.setDisable(true);
        notifBtn.setDisable(true);

        eventColumn.setCellValueFactory(new PropertyValueFactory<EventDTO, String>("event"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<EventDTO, String>("status"));
        notificationColumn.setCellValueFactory(new PropertyValueFactory<EventDTO, String>("notifications"));

        eventsTable.setItems(modelEvents);
        statusComboBox.getSelectionModel().selectFirst();

        notifList.setItems(modelNotif);

        eventsTable.getSelectionModel().selectedItemProperty().addListener(o -> buttonsCases());

        statusComboBox.valueProperty().addListener(o -> {
            filter();
        });
    }

    private void filter() {

        if (statusComboBox.getValue().matches("No Filter"))
            initModel();

        if (statusComboBox.getValue().matches("Status: Going"))
            filteredModel(eventDTO -> eventDTO.getStat());

        if (statusComboBox.getValue().matches("Status: Not going"))
            filteredModel(eventDTO -> !eventDTO.getStat());

        if (statusComboBox.getValue().matches("Notifications: On"))
            filteredModel(eventDTO -> eventDTO.getNotif() && eventDTO.getStat());

        if (statusComboBox.getValue().matches("Notifications: Off"))
            filteredModel(eventDTO -> !eventDTO.getNotif() && eventDTO.getStat());

    }

    private void filteredModel(Predicate<EventDTO> p) {
        modelNotif.setAll(userDashboard.notificateUser());

        modelEvents.setAll(userDashboard
                .findAllEventsDto()
                .stream()
                .filter(p)
                .collect(Collectors.toList()));
    }

    private void buttonsCases() {
        EventDTO event = eventsTable.getSelectionModel().getSelectedItem();
        if (event == null) {
            notifBtn.setText("Notifications");
            statusBtn.setText("Status");

            statusBtn.setDisable(true);
            notifBtn.setDisable(true);
        } else {
            if (event.getStat()) {
                statusBtn.setDisable(false);
                statusBtn.setText("Don't go to event");

                notifBtn.setDisable(false);
                if (event.getNotif()) {
                    notifBtn.setText("Notifications off");
                } else {
                    notifBtn.setText("Notifications on");
                }
            } else {
                statusBtn.setDisable(false);
                statusBtn.setText("Go to event");

                notifBtn.setDisable(true);
                notifBtn.setText("Notifications");
            }
        }
    }

    public void setServices(UserService userService, EventService eventService, User currentUser, FriendshipService frService, MessageService msgServive) {
        userDashboard.setEventService(eventService);
        userDashboard.setUserService(userService);
        userDashboard.setCurrentUser(currentUser);
        userDashboard.setFriendshipService(frService);
        userDashboard.setMessageService(msgServive);

        initModel();

        comboOpt.add("No Filter");
        comboOpt.add("Status: Going");
        comboOpt.add("Status: Not going");
        comboOpt.add("Notifications: On");
        comboOpt.add("Notifications: Off");

        statusComboBox.getItems().setAll(comboOpt);

        userDashboard.eventsServiceAddObserver(this);
    }

    private void initModel() {
        modelNotif.setAll(userDashboard.notificateUser());
        modelEvents.setAll(userDashboard.findAllEventsDto());
        statusComboBox.getSelectionModel().selectFirst();
    }

    @FXML
    public void onActionCreateEvent(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader();
            Stage stage = new Stage();

            loader.setLocation(HomeWindow.class.getResource("views/newEvent-view.fxml"));

            AnchorPane root = loader.load();
            NewEventController controller = loader.getController();
            controller.setServices(userDashboard.getUserService(), userDashboard.getEventService(), userDashboard.getCurrentUser());

            stage.setMinWidth(500);
            stage.setMinHeight(500);
            stage.setScene(new Scene(root));

            stage.setTitle("Tutti frutti - Create Event");
            stage.show();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    private void onActionStatus() {
        EventDTO event = eventsTable.getSelectionModel().getSelectedItem();

        if (statusBtn.getText().matches("Go to event"))
            userDashboard.saveParticipant(event.getEvent().getId());
        else
            userDashboard.deleteParticipant(event.getEvent().getId());

        initModel();
    }

    @FXML
    private void onActionNotification() {
        EventDTO event = eventsTable.getSelectionModel().getSelectedItem();

        userDashboard.updateParticipant(event.getEvent().getId(), !event.getNotif());

        initModel();
    }


    @Override
    public void update(EventsStatusEvent eventsStatusEvent) {
        initModel();
    }
}
