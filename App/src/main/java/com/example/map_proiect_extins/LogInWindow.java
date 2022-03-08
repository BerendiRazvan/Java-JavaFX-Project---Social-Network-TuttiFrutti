package com.example.map_proiect_extins;

import com.example.map_proiect_extins.controller.LoginController;
import com.example.map_proiect_extins.domain.*;
import com.example.map_proiect_extins.repository.Repository;
import com.example.map_proiect_extins.repository.database.*;
import com.example.map_proiect_extins.repository.paging.PagingRepository;
import com.example.map_proiect_extins.service.*;
import com.example.map_proiect_extins.validators.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.CacheHint;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class LogInWindow extends Application {

    Repository<Long, User> userDataBaseRepo;
    Repository<Long, Event> eventDataBaseRepo;
    Repository<Pair<Long>, Friendship> friendshipDataBaseRepo;
    Repository<Pair<Long>, EventParticipant> participantDataBaseRepo;
    MessageDataBaseRepository messageRepository;
    PagingRepository<Long,User> pagingUserRepository;
    PagingRepository<Pair<Long>, Friendship> friendshipPagingRepository;


    @Override
    public void start(Stage stage1) throws IOException {

        //Repositorys
        userDataBaseRepo = new UserDataBaseRepository("jdbc:postgresql://localhost:5432/socialNetwork",
                "postgres", "Razvan28", new UserValidator());
        friendshipDataBaseRepo = new FriendshipDataBaseRepository("jdbc:postgresql://localhost:5432/socialNetwork",
                "postgres", "Razvan28", new FriendshipValidator());
        messageRepository = new MessageDataBaseRepository("jdbc:postgresql://localhost:5432/socialNetwork",
                "postgres", "Razvan28", new MessageValidator());
        pagingUserRepository = new UserDataBaseRepository("jdbc:postgresql://localhost:5432/socialNetwork",
                "postgres", "Razvan28", new UserValidator());
        friendshipPagingRepository = new FriendshipDataBaseRepository("jdbc:postgresql://localhost:5432/socialNetwork",
                "postgres", "Razvan28", new FriendshipValidator());
        eventDataBaseRepo = new EventDataBaseRepository("jdbc:postgresql://localhost:5432/socialNetwork",
                "postgres", "Razvan28", new EventsValidator());
        participantDataBaseRepo = new ParticipantDataBaseRepository("jdbc:postgresql://localhost:5432/socialNetwork",
                "postgres", "Razvan28", new ParticipantValidator());


        //Services
        UserService userService = new UserService(userDataBaseRepo, friendshipDataBaseRepo,pagingUserRepository);
        FriendshipService friendshipService = new FriendshipService(userDataBaseRepo, friendshipDataBaseRepo,friendshipPagingRepository);
        ConnectionService connectionService = new ConnectionService(userDataBaseRepo, friendshipDataBaseRepo);
        MessageService messageService = new MessageService(messageRepository, userDataBaseRepo);
        EventService eventService = new EventService(userDataBaseRepo, eventDataBaseRepo,participantDataBaseRepo);


        //GUI
        FXMLLoader loader1 = new FXMLLoader();
        loader1.setLocation(getClass().getResource("views/login-view.fxml"));

        BorderPane root1 = loader1.load();
        LoginController controller1 = loader1.getController();
        controller1.setService(userService, friendshipService, messageService, eventService);


        stage1.setScene(new Scene(root1));
        stage1.setTitle("Tutti frutti");
        stage1.setMinWidth(770);
        stage1.setMinHeight(510);

        stage1.getIcons().add(new Image(HomeWindow.class.getResource("images/appwlogo.png").toExternalForm()));
        stage1.show();

        //GUI
        Stage stage2 = new Stage();

        FXMLLoader loader2 = new FXMLLoader();
        loader2.setLocation(getClass().getResource("views/login-view.fxml"));

        BorderPane root2 = loader2.load();
        LoginController controller2 = loader2.getController();
        controller2.setService(userService, friendshipService, messageService, eventService);


        stage2.setScene(new Scene(root2));
        stage2.setTitle("Tutti frutti");
        stage2.setMinWidth(770);
        stage2.setMinHeight(510);

        stage2.getIcons().add(new Image(HomeWindow.class.getResource("images/appwlogo.png").toExternalForm()));
        stage2.show();

    }

    public static void main(String[] args) {
        launch();
    }
}

