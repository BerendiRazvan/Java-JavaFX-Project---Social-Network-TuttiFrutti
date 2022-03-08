package com.example.map_proiect_extins.controller;

import com.example.map_proiect_extins.FriendshipsWindow;
import com.example.map_proiect_extins.UserDashboard;
import com.example.map_proiect_extins.domain.Message;
import com.example.map_proiect_extins.domain.User;
import com.example.map_proiect_extins.domain.UserFriendsDTO;
import com.example.map_proiect_extins.service.FriendshipService;
import com.example.map_proiect_extins.service.MessageService;
import com.example.map_proiect_extins.service.UserService;
import com.example.map_proiect_extins.utils.events.MessageStatusEvent;
import com.example.map_proiect_extins.utils.observer.Observer;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;



public class MessagesController implements Observer<MessageStatusEvent> {

    UserDashboard userDashboard = new UserDashboard();

    ObservableList<UserFriendsDTO> modelListFriends = FXCollections.observableArrayList();
    ObservableList<Message> modelConversations = FXCollections.observableArrayList();


    @FXML
    private Button sendBtn;
    @FXML
    private TextArea messageContent;
    @FXML
    private VBox messagesVBox;
    @FXML
    private ScrollPane conversationScrPane;
    @FXML
    private ListView<UserFriendsDTO> conversationsList;

    @FXML
    private Label messageError;


    @FXML
    public void initialize() {
        conversationsList.setItems(modelListFriends);

        clearMsg();
        clearErrors();

        messageContent.textProperty().addListener(o -> messageError.setText(""));

        messagesVBox.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                conversationScrPane.setVvalue((Double) newValue);
            }
        });

        conversationsList.getSelectionModel().selectedItemProperty().addListener(o -> displayConversation());
    }


    private void displayConversation() {
        messagesVBox.getChildren().clear();
        messageContent.setPromptText("Write your message here");

        UserFriendsDTO friend = conversationsList.getSelectionModel().getSelectedItem();
        Iterable<Message> conversation;
        try {
            if (friend.getId().getFirst().equals(userDashboard.getCurrentUserId()))
                conversation = userDashboard.findConversation(friend.getId().getSecond());
            else
                conversation = userDashboard.findConversation(friend.getId().getFirst());

            conversation.forEach(msg -> {
                if (msg.getFrom().getId().equals(userDashboard.getCurrentUserId()))
                    loggedUserMessage(msg);
                else
                    friendMessage(msg);
            });
        } catch (Exception e) {
            messageContent.setPromptText("No messages. Write your first message here");
        }
    }

    @FXML
    private void loggedUserMessage(Message msg) {
        if (!msg.getMessage().isEmpty()) {
            HBox hBoxMessage = new HBox();
            hBoxMessage.setAlignment(Pos.CENTER_RIGHT);

            hBoxMessage.setPadding(new Insets(5, 5, 5, 10));
            Text text = new Text(msg.getMessage());
            TextFlow textFlow = new TextFlow(text);
            textFlow.setStyle("" +
                    "-fx-background-color:  #374785;" +
                    "-fx-background-radius: 20px;");

            textFlow.setPadding(new Insets(5, 10, 5, 10));
            text.setFill(Color.color(0.934, 0.945, 0.996));
            hBoxMessage.getChildren().add(textFlow);
            messagesVBox.getChildren().add(hBoxMessage);
        }
    }

    @FXML
    private void friendMessage(Message msg) {
        if (!msg.getMessage().isEmpty()) {
            HBox hBoxMessage = new HBox();
            hBoxMessage.setAlignment(Pos.CENTER_LEFT);

            hBoxMessage.setPadding(new Insets(5, 5, 5, 10));
            Text text = new Text(msg.getMessage());
            TextFlow textFlow = new TextFlow(text);
            textFlow.setStyle("" +
                    "-fx-background-color:  #e3eff6;" +
                    "-fx-background-radius: 20px;");

            textFlow.setPadding(new Insets(5, 10, 5, 10));
            text.setFill(Color.color(0.2124, 0.2741, 0.5135));
            hBoxMessage.getChildren().add(textFlow);
            messagesVBox.getChildren().add(hBoxMessage);
        }
    }


    private void clearMsg() {
        messageContent.setText("");
    }

    private void clearErrors() {
        messageError.setText("");
    }

    private Long getUserFriendId(UserFriendsDTO fr) {
        if (fr.getId().getSecond().equals(userDashboard.getCurrentUserId()))
            return fr.getId().getFirst();
        else
            return fr.getId().getSecond();
    }

    private LocalDateTime getLastMsgDate(UserFriendsDTO f) {
        List<Message> msgList = new ArrayList<>();
        try {
            userDashboard.findConversation(getUserFriendId(f)).forEach(o -> msgList.add(o));
        } catch (Exception e) {
            return LocalDateTime.of(1900, 12, 16, 7, 45, 55);
        }
        return msgList.get(msgList.size() - 1).getData();
    }

    private List<UserFriendsDTO> getFriendshipsDTOList() {

        return userDashboard.getUserFriendships();
//                .stream()
//                .sorted((f1,f2) -> getLastMsgDate(f2).compareTo(getLastMsgDate(f1)))
//                .toList();
    }

    public void setServices(UserService usrService, FriendshipService frService, User loginUser, MessageService msgService) {
        userDashboard.setUserService(usrService);
        userDashboard.setFriendshipService(frService);
        userDashboard.setCurrentUser(loginUser);
        userDashboard.setMessageService(msgService);

        userDashboard.messageObserverAddObserver(this);
        modelListFriends.setAll(getFriendshipsDTOList());
    }


    @FXML
    public void onActionSendMsg(ActionEvent event) {

        String msg = messageContent.getText();

        if (msg.matches("")) {
            messageError.setText("Message can't be empty!");
        } else {
            String receivers = "";

            UserFriendsDTO recv = conversationsList.getSelectionModel().getSelectedItem();
            if (recv == null) {
                messageError.setText("Please select a conversation!");
            } else {
                if (recv.getId().getFirst().equals(userDashboard.getCurrentUserId()))
                    receivers += recv.getId().getSecond().toString();
                else
                    receivers += recv.getId().getFirst().toString();

                userDashboard.saveAMessage(receivers,msg);
                clearMsg();
            }
        }
    }

    @Override
    public void update(MessageStatusEvent statusEvent) {
        displayConversation();
    }
}
