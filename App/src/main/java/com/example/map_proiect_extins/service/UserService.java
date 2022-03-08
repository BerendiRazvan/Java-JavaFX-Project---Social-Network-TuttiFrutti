package com.example.map_proiect_extins.service;

import com.example.map_proiect_extins.domain.Friendship;
import com.example.map_proiect_extins.domain.Pair;
import com.example.map_proiect_extins.domain.User;
import com.example.map_proiect_extins.domain.UserFriendsDTO;
import com.example.map_proiect_extins.repository.Repository;
import com.example.map_proiect_extins.repository.database.FriendshipDataBaseRepository;
import com.example.map_proiect_extins.repository.paging.Page;
import com.example.map_proiect_extins.repository.paging.Pageable;
import com.example.map_proiect_extins.repository.paging.PageableImplementation;
import com.example.map_proiect_extins.repository.paging.PagingRepository;
import com.example.map_proiect_extins.utils.events.ChangeEventType;
import com.example.map_proiect_extins.utils.events.FriendshipStatusEvent;
import com.example.map_proiect_extins.utils.events.MessageStatusEvent;
import com.example.map_proiect_extins.utils.events.UserStatusEvent;
import com.example.map_proiect_extins.utils.observer.Observable;
import com.example.map_proiect_extins.utils.observer.Observer;
import com.example.map_proiect_extins.validators.ServiceException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

//stocarea in fisier sau in memory a datelor pe care le primesti de la UI
public class UserService implements Observable<UserStatusEvent> {
    private Repository<Long, User> userRepository;
    private Repository<Pair<Long>, Friendship> friendshipRepository;
    private PagingRepository<Long, User> pagingUserRepository;

    public UserService(Repository<Long, User> userRepository, Repository<Pair<Long>, Friendship> friendshipRepository, PagingRepository<Long, User> pagingRepository) {
        this.userRepository = userRepository;
        this.friendshipRepository = friendshipRepository;
        this.pagingUserRepository = pagingRepository;
    }

    public User logInUser(String email, String password) {
        if (email.length() == 0)
            throw new ServiceException("Please enter an email");
        if (password.length() == 0)
            throw new ServiceException("Please enter a password");

        User loggedInUser = new User("fname", "lname", "email@gmail.com", "pass");
        userRepository.findAll().forEach(user -> {
            if (user.getEmail().matches(email)) {
                if (!user.getPassword().matches(password))
                    throw new ServiceException("Incorrect password!");
                else {
                    loggedInUser.setId(user.getId());
                    loggedInUser.setFirstName(user.getFirstName());
                    loggedInUser.setLastName(user.getLastName());
                    loggedInUser.setEmail(user.getEmail());
                    loggedInUser.setPassword(user.getPassword());
                }
            }
        });
        if (loggedInUser.getId() == null) {
            throw new ServiceException("Invalid mail, this account does not exist!");
        }

        return loggedInUser;
    }

    public void save(String firstName, String lastName, String email, String password) {
        Iterable<User> result = userRepository.findAll();

        userRepository.findAll().forEach(user -> {
            if (user.getEmail().matches(email))
                throw new ServiceException("Email existent!");
        });

        User lastUser = null;
        for (User value : result) {
            lastUser = value;
        }

        User user = new User(firstName, lastName, email, password);
        if (lastUser != null) {
            user.setId(lastUser.getId() + 1);
        } else {
            user.setId(1L);
        }
        userRepository.save(user);
        notifyObservers(new UserStatusEvent(ChangeEventType.ADD,user));
    }

    public Iterable<User> findAll() {
        return userRepository.findAll();
    }

    public void delete(Long id) {
        User deletedUser = userRepository.findOne(id);
        if (deletedUser == null)
            throw new ServiceException("No user with the given id");

        for (User user : deletedUser.getFriends()) {
            user.getFriends().removeIf(friend ->
                    Objects.equals(friend.getId(), deletedUser.getId())
            );
        }
        List<Friendship> friendshipList = new ArrayList<>();

        for (Friendship friendship : ((FriendshipDataBaseRepository) friendshipRepository).findAllRequests()) {
            if (friendship.getId().getFirst() == id || friendship.getId().getSecond() == id) //conteaza daca prietenia ta e 1,2 sau 2,1, nu, tot o stergi
                friendshipList.add(friendship);
        }

        for (Friendship friendship : friendshipList) {
            friendshipRepository.delete(friendship.getId());
        }
        userRepository.delete(id);
    }


    public void update(Long id, String firstName, String lastName, String email, String password) {
        User user = new User(firstName, lastName, email, password);
        user.setId(id);
        userRepository.update(user);
    }

    private int page = 0;
    private int size = 6;

    public int getSize() {
        return size;
    }

    private Pageable pageable;

    public void setPage(int page) {
        this.page = page;
    }

    public int getPage() {
        return page;
    }

    public Set<User> getPreviousPageOfUsers(Long currentUserId) {
        if (this.page >= 1) {
            this.page--;
            return getUsersOnPage(this.page, currentUserId);
        }
        return null;
    }

    public Set<User> existNextPageOfUsers (Long currentUserId) {
        int pageAux = this.page;
        pageAux++;
        Set<User> users = getUsersOnPage(pageAux,currentUserId);
        if (users.size() != 0) {
            return users;
        }
        return null;
    }

    public Set<User> getNextPageOfUsers(Long currentUserId) {
        this.page++;
        Set<User> users = getUsersOnPage(this.page,currentUserId);
        if (users.size() != 0) {
            return users;
        }
        this.page--;
        return null;
    }

    public int checkNextUsers(Long currentUserId) {
        int pageAux = this.page;
        Set<User> users = getUsersOnPage(pageAux, currentUserId);
        pageAux++;
        Set<User> nextUsers = getUsersOnPage(pageAux, currentUserId);
        if (users.size() != 0) {
            if (users.size() == size && nextUsers.size() == 0) {
                return -1;
            }
        }
        return 0;
    }

    public Set<User> getUsersOnPage(int page, Long currentUserId) {
        //this.page = page;
        Pageable pageable = new PageableImplementation(page, this.size);
        Page<User> studentPage = pagingUserRepository.findAllPaginated(pageable, currentUserId);
        return studentPage.getContent().collect(Collectors.toSet());
    }

    private List<Observer<UserStatusEvent>> observers=new ArrayList<>();

    @Override
    public void addObserver(Observer<UserStatusEvent> e) {
        observers.add(e);
    }

    @Override
    public void removeObserver(Observer<UserStatusEvent> e) {
        observers.remove(e);
    }

    @Override
    public void notifyObservers(UserStatusEvent t) {
        observers.stream().forEach(x->x.update(t));
    }

}
