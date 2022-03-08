package com.example.map_proiect_extins;

import com.example.map_proiect_extins.controller.FrienshipsController;
import com.example.map_proiect_extins.controller.HomeController;
import com.example.map_proiect_extins.controller.LoginController;
import com.example.map_proiect_extins.domain.*;
import com.example.map_proiect_extins.repository.Repository;
import com.example.map_proiect_extins.repository.database.*;
import com.example.map_proiect_extins.repository.paging.PagingRepository;
import com.example.map_proiect_extins.service.*;
import com.example.map_proiect_extins.validators.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

public class HomeWindow extends Application {
    Repository<Long, User> userDataBaseRepo;
    Repository<Pair<Long>, Friendship> friendshipDataBaseRepo;
    MessageDataBaseRepository messageRepository;
    PagingRepository<Long,User> pagingUserRepository;
    PagingRepository<Pair<Long>, Friendship> friendshipPagingRepository;
    Repository<Long, Event> eventDataBaseRepo;
    Repository<Pair<Long>, EventParticipant> participantDataBaseRepo;

    @Override
    public void start(Stage stage) throws IOException {

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
        EventService eventService = new EventService(userDataBaseRepo, eventDataBaseRepo, participantDataBaseRepo);

        //GUI
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("views/home-view.fxml"));

        BorderPane root = loader.load();
        HomeController controller = loader.getController();

        User user;
        Long id = 1L;
        user = userDataBaseRepo.findOne(id);
        controller.setService(friendshipService, userService, user, messageService, eventService);

        stage.setScene(new Scene(root));
        stage.setTitle("Social Network - Friendships");

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}