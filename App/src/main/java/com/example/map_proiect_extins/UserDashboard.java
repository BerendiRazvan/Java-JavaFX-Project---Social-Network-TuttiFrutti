package com.example.map_proiect_extins;

import com.example.map_proiect_extins.domain.*;
import com.example.map_proiect_extins.service.EventService;
import com.example.map_proiect_extins.service.FriendshipService;
import com.example.map_proiect_extins.service.MessageService;
import com.example.map_proiect_extins.service.UserService;
import com.example.map_proiect_extins.utils.events.EventsStatusEvent;
import com.example.map_proiect_extins.utils.events.FriendshipStatusEvent;
import com.example.map_proiect_extins.utils.events.MessageStatusEvent;
import com.example.map_proiect_extins.utils.events.UserStatusEvent;
import com.example.map_proiect_extins.utils.observer.Observer;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public class UserDashboard {

    private UserService userService;
    private FriendshipService friendshipService;
    private MessageService messageService;
    private User currentUser;
    private EventService eventService;



    public UserDashboard() {
    }


    public List<String> raportMessagesInAPeriodForAFriendMSG(Long frId, LocalDate dateS,LocalDate dateF){
        return messageService.raportMessagesInAPeriodForAFriend(currentUser.getId(), frId,dateS, dateF);
    }

    public List<String> raportMessagesInAPeriodForAFriend(LocalDate dateS,LocalDate dateF){
        return messageService.raportMessagesInAPeriod(currentUser.getId(), dateS, dateF);
    }

    public List<String> raportFriendshipsInAPeriod(LocalDate dateS,LocalDate dateF){
        return friendshipService.raportFriendshipsInAPeriod(currentUser.getId(), dateS, dateF);
    }

    public int checkNextUsers() {
        return userService.checkNextUsers(currentUser.getId());
    }

    public Set<User> getPreviousPageOfUsers() {
        return userService.getPreviousPageOfUsers(currentUser.getId());
    }

    public Set<User> getNextPageOfUsers() {
        return userService.getNextPageOfUsers(currentUser.getId());
    }

    public List<UserFriendsDTO> getUserFriendships() {
        return friendshipService.getUserFriendships(currentUser.getId());
    }

    public Iterable<Message> findConversation(Long friendId) {
        return messageService.findConversation(currentUser.getId(), friendId);
    }

    public void saveAMessage(String receivers, String msg) {
        messageService.save(currentUser.getId(), receivers, msg, -1L);
    }

    public void updateRequest(FriendshipDTO friendshipSelected) {
        friendshipService.updateRequest(friendshipSelected.getId().getFirst(), friendshipSelected.getId().getSecond(), 1);

    }

    public List<FriendshipDTO> findRequestsDTO() {
        return friendshipService.findRequestsDTO(currentUser.getId());
    }

    public List<EventDTO> findAllEventsDto() {
        return eventService.findAllEventsDto(currentUser);
    }


    public void saveAUser(TextField firstNameTextField, TextField lastNameTextField, TextField emailTextField, TextField passwordTextField, TextField passwordVerifTextField) {
        userService.save(firstNameTextField.getText(), lastNameTextField.getText(), emailTextField.getText(), passwordTextField.getText());

    }

    public void saveParticipant(Long idEvent){
        eventService.saveParticipant(currentUser.getId(), idEvent);

    }

    public List<NotifDTO> notificateUser(){
        return eventService.notificateUser(currentUser);
    }

    public void deleteParticipant(Long idEvent){
        eventService.deleteParticipant(new Pair<>(currentUser.getId(), idEvent));
    }

    public void updateParticipant(Long idEvent, Boolean notif){
        eventService.updateParticipant(currentUser.getId(), idEvent, notif);
    }

    public User logInUser(TextField emailTextField, PasswordField passwordTextField) {
        return userService.logInUser(emailTextField.getText(), passwordTextField.getText());
    }

    public List<FriendshipDTO> findCurrentUserRequests() {
        return friendshipService.findCurrentUserRequests(currentUser.getId());
    }

    public void userServiceAddObserver(Observer<UserStatusEvent> e) {
        userService.addObserver(e);
    }

    public void messageObserverAddObserver(Observer<MessageStatusEvent> e) {
        messageService.addObserver(e);
    }

    public void friendshipServiceAddObserver(Observer<FriendshipStatusEvent> e) {
        friendshipService.addObserver(e);
    }

    public void eventsServiceAddObserver(Observer<EventsStatusEvent> e) {
        eventService.addObserver(e);
    }

    public List<UserFriendsDTO> getPreviousPageOfFriends() {
        return friendshipService.getPreviousPageOfFriends(currentUser.getId());
    }

    public int checkNextFriends() {
        return friendshipService.checkNextFriends(currentUser.getId());
    }

    public List<UserFriendsDTO> getNextPageOfFriends() {
        return friendshipService.getNextPageOfFriends(currentUser.getId());
    }

    public void removeFriendFriendshipDto(FriendshipDTO userSelected) {
        friendshipService.removeFriend(userSelected.getId().getFirst(), userSelected.getId().getSecond());

    }

    public void saveEvent(String name, String description , LocalDateTime startEvent, LocalDateTime finishEvent, User organiser){
        getEventService().saveEvent(name, description, startEvent, finishEvent, organiser);
    }

    public void removeFriendUserFriendsDTO(UserFriendsDTO friendship) {
        friendshipService.removeFriend(friendship.getId().getFirst(), friendship.getId().getSecond());
    }

    public int getFriendshipServiceSize() {
        return friendshipService.getSize();
    }

    public void addFriend(Long id) {
        friendshipService.addFriend(currentUser.getId(), id);
    }

    public int getUserServicePage() {
        return userService.getPage();
    }

    public int getUserServiceSize() {
        return userService.getSize();
    }

    public void setUserServicePage(int page) {
        userService.setPage(page);
    }

    public void setFriendshipServicePage(int page) {
        friendshipService.setPage(page);
    }

    public int getFriendshipServicePage() {
        return friendshipService.getPage();
    }

    public Set<User> existNextPageOfUsers() {
        return userService.existNextPageOfUsers(currentUser.getId());
    }

    public List<UserFriendsDTO> existsNextPageOfFriends() {
        return friendshipService.existNextPageOfFriends(currentUser.getId());
    }

    public List<UserFriendsDTO> getFriendsOnPage() {
        return friendshipService.getFriendsOnPage(friendshipService.getPage(), currentUser.getId());
    }

    public Set<User> getUsersOnPage() {
        return userService.getUsersOnPage(userService.getPage(), currentUser.getId());
    }

    public Long getCurrentUserId() {
        return currentUser.getId();
    }

    public String getCurrentUserFirstName() {
        return currentUser.getFirstName();
    }

    public String getCurrentUserLastName() {
        return currentUser.getLastName();
    }

    public UserService getUserService() {
        return userService;
    }

    public FriendshipService getFriendshipService() {
        return friendshipService;
    }

    public MessageService getMessageService() {
        return messageService;
    }

    public EventService getEventService() {
        return eventService;
    }

    public void setEventService(EventService eventService) {
        this.eventService = eventService;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setFriendshipService(FriendshipService friendshipService) {
        this.friendshipService = friendshipService;
    }

    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }
}
