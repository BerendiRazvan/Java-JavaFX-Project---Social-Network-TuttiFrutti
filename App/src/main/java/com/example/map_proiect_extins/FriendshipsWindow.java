package com.example.map_proiect_extins;

import com.example.map_proiect_extins.controller.FrienshipsController;
import com.example.map_proiect_extins.controller.LoginController;
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
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class FriendshipsWindow extends Application {
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


        //Services
        UserService userService = new UserService(userDataBaseRepo, friendshipDataBaseRepo,pagingUserRepository);
        FriendshipService friendshipService = new FriendshipService(userDataBaseRepo, friendshipDataBaseRepo,friendshipPagingRepository);
        ConnectionService connectionService = new ConnectionService(userDataBaseRepo, friendshipDataBaseRepo);
        MessageService messageService = new MessageService(messageRepository, userDataBaseRepo);


        //GUI
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("views/friendships-view.fxml"));

        AnchorPane root = loader.load();
        FrienshipsController controller = loader.getController();

        User user;
        Long id = 1L;
        user = userDataBaseRepo.findOne(id);
        controller.setService(friendshipService, userService, user, messageService);

        stage.setScene(new Scene(root));
        stage.setTitle("Social Network - Friendships");

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}