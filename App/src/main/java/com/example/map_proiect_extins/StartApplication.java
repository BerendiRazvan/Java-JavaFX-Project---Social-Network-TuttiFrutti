package com.example.map_proiect_extins;

import com.example.map_proiect_extins.controller.FriendRequestsController;
import com.example.map_proiect_extins.controller.RequestFriendshipController;
import com.example.map_proiect_extins.domain.Friendship;
import com.example.map_proiect_extins.domain.Pair;
import com.example.map_proiect_extins.domain.User;
import com.example.map_proiect_extins.repository.Repository;
import com.example.map_proiect_extins.repository.database.FriendshipDataBaseRepository;
import com.example.map_proiect_extins.repository.database.MessageDataBaseRepository;
import com.example.map_proiect_extins.repository.database.UserDataBaseRepository;
import com.example.map_proiect_extins.repository.paging.PagingRepository;
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

public class StartApplication extends Application {
    Repository<Long, User> userDataBaseRepo;
    Repository<Pair<Long>, Friendship> friendshipDataBaseRepo ;
    MessageDataBaseRepository messageRepository;
    UserService userService;
    FriendshipService friendshipService;
    MessageService messageService;
    PagingRepository<Long,User> pagingUserRepository;
    PagingRepository<Pair<Long>, Friendship> friendshipPagingRepository;



    public static void main(String[] args) {
        //System.out.println("ok");
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        userDataBaseRepo = new UserDataBaseRepository("jdbc:postgresql://localhost:5432/socialNetwork",
                "postgres", "postgres", new UserValidator());
        friendshipDataBaseRepo = new FriendshipDataBaseRepository("jdbc:postgresql://localhost:5432/socialNetwork",
                "postgres", "postgres", new FriendshipValidator());
        messageRepository = new MessageDataBaseRepository("jdbc:postgresql://localhost:5432/socialNetwork",
                "postgres","postgres",new MessageValidator());
        pagingUserRepository = new UserDataBaseRepository("jdbc:postgresql://localhost:5432/socialNetwork",
                "postgres", "postgres", new UserValidator());
        friendshipPagingRepository = new FriendshipDataBaseRepository("jdbc:postgresql://localhost:5432/socialNetwork",
                "postgres", "postgres", new FriendshipValidator());


        userService = new UserService(userDataBaseRepo,friendshipDataBaseRepo,pagingUserRepository);
        friendshipService = new FriendshipService(userDataBaseRepo,friendshipDataBaseRepo,friendshipPagingRepository);
        messageService = new MessageService(messageRepository,userDataBaseRepo);

        //userService.findAll().forEach(System.out::println);

        initViewFriendRequests(primaryStage);
        //initView(primaryStage);
        primaryStage.show();


    }

    private void initView(Stage primaryStage) throws IOException {

        FXMLLoader userLoader = new FXMLLoader();
        userLoader.setLocation(getClass().getResource("views/requestFriendship-view.fxml"));
        AnchorPane userLayout = userLoader.load();
        primaryStage.setScene(new Scene(userLayout));

        RequestFriendshipController requestFriendshipController = userLoader.getController();
        User user;
        Long id = 5L;
        user = userDataBaseRepo.findOne(id);
        requestFriendshipController.setServices(userService,friendshipService,user, messageService);
    }

    private void initViewFriendRequests(Stage primaryStage) throws IOException {

        FXMLLoader friendLoader = new FXMLLoader();
        friendLoader.setLocation(getClass().getResource("views/friendRequests-view.fxml"));
        AnchorPane friendLayout = friendLoader.load();
        primaryStage.setScene(new Scene(friendLayout));

        FriendRequestsController friendRequestsController = friendLoader.getController();
        User user;
        Long id = 5L;
        user = userDataBaseRepo.findOne(id);
        friendRequestsController.setServices(userService,friendshipService,user, messageService);

    }
}

