package com.example.map_proiect_extins;

import com.example.map_proiect_extins.domain.Friendship;
import com.example.map_proiect_extins.domain.Pair;
import com.example.map_proiect_extins.domain.User;
import com.example.map_proiect_extins.repository.Repository;
import com.example.map_proiect_extins.repository.database.FriendshipDataBaseRepository;
import com.example.map_proiect_extins.repository.database.MessageDataBaseRepository;
import com.example.map_proiect_extins.repository.database.UserDataBaseRepository;
import com.example.map_proiect_extins.repository.paging.PagingRepository;
import com.example.map_proiect_extins.service.ConnectionService;
import com.example.map_proiect_extins.service.FriendshipService;
import com.example.map_proiect_extins.service.MessageService;
import com.example.map_proiect_extins.service.UserService;
import com.example.map_proiect_extins.validators.FriendshipValidator;
import com.example.map_proiect_extins.validators.MessageValidator;

import com.example.map_proiect_extins.validators.UserValidator;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {

    Repository<Long, User> userDataBaseRepo;
    Repository<Pair<Long>, Friendship> friendshipDataBaseRepo;
    MessageDataBaseRepository messageRepository;
    PagingRepository<Long,User> pagingUserRepository;
    PagingRepository<Pair<Long>, Friendship> friendshipPagingRepository;

    @Override
    public void start(Stage stage) throws IOException {

        //Repositorys
        userDataBaseRepo = new UserDataBaseRepository("jdbc:postgresql://localhost:5432/socialNetwork",
                "postgres", "postgres", new UserValidator());
        friendshipDataBaseRepo = new FriendshipDataBaseRepository("jdbc:postgresql://localhost:5432/socialNetwork",
                "postgres", "postgres", new FriendshipValidator());
        messageRepository = new MessageDataBaseRepository("jdbc:postgresql://localhost:5432/socialNetwork",
                "postgres", "postgres", new MessageValidator());
        pagingUserRepository = new UserDataBaseRepository("jdbc:postgresql://localhost:5432/socialNetwork",
                "postgres", "postgres", new UserValidator());
        friendshipPagingRepository = new FriendshipDataBaseRepository("jdbc:postgresql://localhost:5432/socialNetwork",
                "postgres", "postgres", new FriendshipValidator());

        userDataBaseRepo.findAll().forEach(System.out::println);
        //Services
        UserService userService = new UserService(userDataBaseRepo, friendshipDataBaseRepo,pagingUserRepository);
        FriendshipService friendshipService = new FriendshipService(userDataBaseRepo, friendshipDataBaseRepo,friendshipPagingRepository);
        ConnectionService connectionService = new ConnectionService(userDataBaseRepo, friendshipDataBaseRepo);
        MessageService messageService = new MessageService(messageRepository, userDataBaseRepo);

        //GUI
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Hello!");

        //stage.getIcons().add(new Image("img/social-media.png"));
        stage.setScene(scene);
        stage.show();

    }

    public static void main(String[] args) {
        launch();
    }
}