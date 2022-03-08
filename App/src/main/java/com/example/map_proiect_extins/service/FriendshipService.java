package com.example.map_proiect_extins.service;

import com.example.map_proiect_extins.domain.*;
import com.example.map_proiect_extins.repository.Repository;
import com.example.map_proiect_extins.repository.database.FriendshipDataBaseRepository;
import com.example.map_proiect_extins.repository.paging.Page;
import com.example.map_proiect_extins.repository.paging.Pageable;
import com.example.map_proiect_extins.repository.paging.PageableImplementation;
import com.example.map_proiect_extins.repository.paging.PagingRepository;
import com.example.map_proiect_extins.utils.events.ChangeEventType;
import com.example.map_proiect_extins.utils.events.FriendshipStatusEvent;
import com.example.map_proiect_extins.utils.observer.Observable;
import com.example.map_proiect_extins.utils.observer.Observer;
import com.example.map_proiect_extins.validators.ServiceException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class FriendshipService implements Observable<FriendshipStatusEvent> {
    private Repository<Long, User> userRepository;
    private Repository<Pair<Long>, Friendship> friendshipRepository;
    private PagingRepository<Pair<Long>, Friendship> friendshipPagingRepository;

    public FriendshipService(Repository<Long, User> userRepository, Repository<Pair<Long>, Friendship> friendshipRepository, PagingRepository<Pair<Long>, Friendship> friendshipPagingRepository) {
        this.userRepository = userRepository;
        this.friendshipRepository = friendshipRepository;
        this.friendshipPagingRepository = friendshipPagingRepository;
        //updateUsersFriendships();
    }

    private void updateUsersFriendships() {
        for (Friendship friendship : friendshipRepository.findAll()) {
            User user1 = userRepository.findOne(friendship.getId().getFirst());
            User user2 = userRepository.findOne(friendship.getId().getSecond());
            user1.addFriend(user2);
            user2.addFriend(user1);
        }
    }

    public void addFriend(Long id1, Long id2) {
        User user1 = userRepository.findOne(id1);
        User user2 = userRepository.findOne(id2);
        if (user1 == null || user2 == null)
            throw new ServiceException("User does not exist,please input valid ids");

        if (friendshipRepository.findOne(new Pair(id1, id2)) != null || friendshipRepository.findOne(new Pair(id2, id1)) != null)
            throw new ServiceException("Friendship already exist,please input valid ids");

        user1.addFriend(user2);
        user2.addFriend(user1);
        Friendship friendship1 = new Friendship(new Pair<>(user1.getId(), user2.getId()));
        friendshipRepository.save(friendship1);
        userRepository.update(user1);
        userRepository.update(user2);


        notifyObservers(new FriendshipStatusEvent(ChangeEventType.ADD,friendship1));
    }

    public void removeFriend(Long id1, Long id2) {
        User user1 = userRepository.findOne(id1);
        User user2 = userRepository.findOne(id2);
        if (user1 == null || user2 == null)
            throw new ServiceException("User does not exist,please input valid ids");

        if (friendshipRepository.findOne(new Pair(id1, id2)) == null && friendshipRepository.findOne(new Pair(id2, id1)) == null)
            throw new ServiceException("Friendship does not  exist,please input valid ids");

        Friendship friendshipR = friendshipRepository.findOne((new Pair(id1, id2)));

        user1.removeFriend(user2);
        user2.removeFriend(user1);
        friendshipRepository.delete(new Pair(user1.getId(), user2.getId()));
        friendshipRepository.delete(new Pair(user2.getId(), user1.getId()));
        userRepository.update(user1);
        userRepository.update(user2);


        notifyObservers(new FriendshipStatusEvent(ChangeEventType.DELETE,friendshipR));
    }

    /**
     * Filter friendships
     *
     * @param list - List of friendships
     * @param p    - Predicate for filter
     * @return List of a person's friends
     */
    private List<Friendship> filterFriendships(List<Friendship> list, Predicate<Friendship> p) {
        return list
                .stream()
                .filter(p)
                .toList();
    }


    /**
     * Makes a string list from a users list
     *
     * @param list - List of friendships
     * @param id   - User id
     * @return List of a person's friends as String
     */
    private List<String> makeFilterString(List<Friendship> list, Long id) {
        return list.stream()
                .map(f -> {
                    if (f.getId().getFirst().equals(id))
                        return new FilterFriendshipsDTO(userRepository.findOne(f.getId().getSecond()).getFirstName(),
                                userRepository.findOne(f.getId().getSecond()).getLastName(),
                                f.getDate()).toString();
                    else
                        return new FilterFriendshipsDTO(userRepository.findOne(f.getId().getFirst()).getFirstName(),
                                userRepository.findOne(f.getId().getFirst()).getLastName(),
                                f.getDate()).toString();
                })
                .toList();
    }

    /**
     * Makes a FilterDTO list from a users list
     *
     * @param list - List of friendships
     * @param id   - User id
     * @return List of a person's friends as String
     */
    private List<UserFriendsDTO> makeFriendshipDTO(List<Friendship> list, Long id) {
        return list.stream()
                .map(f -> {
                    if (f.getId().getFirst().equals(id))
                        return new UserFriendsDTO(userRepository.findOne(f.getId().getSecond()).getFirstName(),
                                userRepository.findOne(f.getId().getSecond()).getLastName(),
                                f.getDate(), f.getId());
                    else
                        return new UserFriendsDTO(userRepository.findOne(f.getId().getFirst()).getFirstName(),
                                userRepository.findOne(f.getId().getFirst()).getLastName(),
                                f.getDate(), f.getId());
                })
                .toList();
    }

    /**
     * Filters friends of a person
     *
     * @param id - Long, person ID
     * @return The person's friends
     * @throws ServiceException if id is invalid
     */
    public List<String> filterUserFriendships(Long id) {
        if (userRepository.findOne(id) == null)
            throw new ServiceException("Invaid ID");

        List<Friendship> list = new ArrayList<>();
        Predicate<Friendship> predicate = friendship -> friendship.getId().getFirst().equals(id) || friendship.getId().getSecond().equals(id);
        friendshipRepository.findAll().forEach(friendship -> list.add(friendship));
        return makeFilterString(filterFriendships(list, predicate), id);
    }

    private List<Friendship> filterFriendshipsWithDates(List<Friendship> list, Predicate<Friendship> p) {
        return list
                .stream()
                .filter(p)
                .sorted((f1,f2) -> f2.getDate().compareTo(f1.getDate()) )
                .toList();
    }


    public List<String> raportFriendshipsInAPeriod(Long idUser, LocalDate DateS, LocalDate DateF){
        if (userRepository.findOne(idUser) == null)
            throw new ServiceException("Invaid ID");
        if (DateS.isAfter(DateF))
            throw new ServiceException("Invaid dates");

        List<Friendship> list = new ArrayList<>();
        Predicate<Friendship> predicate = friendship ->
                (friendship.getId().getFirst().equals(idUser)||friendship.getId().getSecond().equals(idUser))
                &&(friendship.getDate().isAfter(DateS))
                &&(friendship.getDate().isBefore(DateF));
        friendshipRepository.findAll().forEach(friendship -> list.add(friendship));

        return makeFilterString(filterFriendshipsWithDates(list, predicate), idUser);
    }

    /**
     * Filters friends of a person
     *
     * @param id - Long, person ID
     * @return The person's friends
     * @throws ServiceException if id is invalid
     */
    public List<UserFriendsDTO> getUserFriendships(Long id) {
        if (userRepository.findOne(id) == null)
            throw new ServiceException("Invaid ID");

        List<Friendship> list = new ArrayList<>();
        Predicate<Friendship> predicate = friendship -> friendship.getId().getFirst().equals(id) || friendship.getId().getSecond().equals(id);
        friendshipRepository.findAll().forEach(friendship -> list.add(friendship));
        return makeFriendshipDTO(filterFriendships(list, predicate), id);
    }


    /**
     * Filters friends of a person by month
     *
     * @param id
     * @param m
     * @return The person's friends
     * @throws ServiceException if id or month are invalid
     */
    public List<String> filterUserMonthFriendships(Long id, int m) {
        if (m < 1 || m > 12)
            throw new ServiceException("Invaid month");

        if (userRepository.findOne(id) == null)
            throw new ServiceException("Invaid ID");

        List<Friendship> list = new ArrayList<>();
        Predicate<Friendship> predicate = friendship -> (friendship.getId().getFirst().equals(id) || friendship.getId().getSecond().equals(id)) && friendship.getDate().getMonthValue() == m;
        friendshipRepository.findAll().forEach(friendship -> list.add(friendship));
        return makeFilterString(filterFriendships(list, predicate), id);
    }

    /**
     * Will give you all friendship requests
     *
     * @param id
     * @return all friend requests
     * @throws ServiceException - invalid id
     */
    public List<Friendship> findRequests(Long id) {

        if (userRepository.findOne(id) == null)
            throw new ServiceException("Invaid ID");

        List<Friendship> requestsList = new ArrayList<>();
        ((FriendshipDataBaseRepository) friendshipRepository)
                .findAllRequests()
                .forEach(request -> requestsList.add(request));

        return requestsList
                .stream()
                .filter(friendship -> friendship.getStatus().equals("pending") && (friendship.getId().getSecond().equals(id)))
                .toList();
    }

    public List<FriendshipDTO> findRequestsDTO(Long userId) {
        List<Friendship> friendRequests = findRequests(userId);
        return friendRequests.stream().map(friendship -> {
            Long friendId = friendship.getId().getFirst().equals(userId) ? friendship.getId().getSecond() : friendship.getId().getFirst();
            User friend = userRepository.findOne(friendId);
            return new FriendshipDTO(friendship.getId(), friendship.getDate(),
                    friendship.getStatus(), friend.getFirstName(), friend.getLastName());
        }).collect(Collectors.toList());
    }

    public List<FriendshipDTO> findCurrentUserRequests(Long userId) {
        if (userRepository.findOne(userId) == null)
            throw new ServiceException("Invaid ID");

        List<Friendship> requestsList = new ArrayList<>();
        ((FriendshipDataBaseRepository) friendshipRepository)
                .findAllRequests()
                .forEach(request -> requestsList.add(request));

        Predicate<Friendship> userRequestsPredicate = friendship -> friendship.getStatus().equals("pending") && (friendship.getId().getFirst().equals(userId));
        return requestsList
                .stream()
                .filter(userRequestsPredicate)
                .map(friendship -> {
                    Long friendId = friendship.getId().getFirst().equals(userId) ? friendship.getId().getSecond() : friendship.getId().getFirst();
                    User friend = userRepository.findOne(friendId);
                    return new FriendshipDTO(friendship.getId(), friendship.getDate(),
                            friendship.getStatus(), friend.getFirstName(), friend.getLastName());
                }).collect(Collectors.toList());

    }


    /**
     * Modifies the friendship request with accept or reject
     *
     * @param myId
     * @param friendId
     * @param opt
     * @throws ServiceException - invalid IDs and invalid option
     */
    public void modifyRequest(Long myId, Long friendId, int opt) {
        String status = "";
        if (opt == 1) {
            status = "approved";
        } else if (opt == 2) {
            status = "rejected";
        } else {
            throw new ServiceException("Optiune invalida!");
        }

        Friendship friendship = new Friendship(new Pair<>(friendId, myId));
        friendship.setDate(LocalDate.now());
        friendship.setStatus(status);

        if (!friendshipRepository.findOne(friendship.getId()).getStatus().equals("pending"))
            throw new ServiceException("Invaid friendship request");
        else
            friendshipRepository.update(friendship);
    }

    public void updateRequest(Long friendId, Long userId, int option) {
        Friendship friendship = friendshipRepository.findOne(new Pair<>(friendId, userId));
        if (option == 1) {
            friendship.setStatus("approved");
        } else {
            friendship.setStatus("rejected");

        }
        friendshipRepository.update(friendship);
        notifyObservers(new FriendshipStatusEvent(ChangeEventType.UPDATE,friendship));
    }

    public Iterable<Friendship> findAll() {
        return friendshipRepository.findAll();
    }

    public Friendship findOne(Long id1, Long id2) {
        return friendshipRepository.findOne(new Pair<>(id1, id2));
    }

    public Friendship updateFr(Friendship friendship) {
        return friendshipRepository.update(friendship);
    }

    private int page = 0;
    private int size = 6;

    public int getSize() {
        return size;
    }

    private Pageable pageable;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public List<UserFriendsDTO> getPreviousPageOfFriends(Long currentUserId) {
        if (this.page >= 1) {
            this.page--;
            return getFriendsOnPage(this.page, currentUserId);
        }
        return null;
    }

    public List<UserFriendsDTO> existNextPageOfFriends (Long currentUserId) {
        int pageAux = this.page;
        pageAux++;
        List<UserFriendsDTO> userFriends = getFriendsOnPage(pageAux,currentUserId);
        if (userFriends.size() != 0) {
            return userFriends;
        }
        return null;
    }

    public List<UserFriendsDTO> getNextPageOfFriends (Long currentUserId) {
        this.page++;
        List<UserFriendsDTO> userFriends = getFriendsOnPage(this.page,currentUserId);
        if (userFriends.size() != 0) {
            return userFriends;
        }
        this.page--;
        return null;
    }

    public int checkNextFriends(Long currentUserId) {
        int pageAux = this.page;
        List<UserFriendsDTO> userFriends = getFriendsOnPage(pageAux, currentUserId);
        pageAux++;
        List<UserFriendsDTO> nextUsers = getFriendsOnPage(pageAux, currentUserId);
        if (userFriends.size() != 0) {
            if (userFriends.size() == size && nextUsers.size() == 0) {
                return -1;
            }
        }
        return 0;
    }

    public List<UserFriendsDTO> getFriendsOnPage(int page, Long currentUserId) {
        //this.page = page;
        Pageable pageable = new PageableImplementation(page, this.size);
        Page<Friendship> studentPage = friendshipPagingRepository.findAllPaginated(pageable, currentUserId);

        List<Friendship> userFriends =  studentPage.getContent().collect(Collectors.toList());
        return makeFriendshipDTO(userFriends,currentUserId);
    }

    private List<Observer<FriendshipStatusEvent>> observers=new ArrayList<>();

    @Override
    public void addObserver(Observer<FriendshipStatusEvent> e) {
        observers.add(e);
    }

    @Override
    public void removeObserver(Observer<FriendshipStatusEvent> e) {
        observers.remove(e);
    }

    @Override
    public void notifyObservers(FriendshipStatusEvent t) {
        observers.stream().forEach(x->x.update(t));
    }
}
