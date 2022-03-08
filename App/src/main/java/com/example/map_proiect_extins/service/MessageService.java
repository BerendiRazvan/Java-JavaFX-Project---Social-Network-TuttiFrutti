package com.example.map_proiect_extins.service;

import com.example.map_proiect_extins.domain.*;
import com.example.map_proiect_extins.repository.Repository;
import com.example.map_proiect_extins.repository.database.MessageDataBaseRepository;
import com.example.map_proiect_extins.utils.events.ChangeEventType;
import com.example.map_proiect_extins.utils.events.MessageStatusEvent;
import com.example.map_proiect_extins.utils.observer.Observable;
import com.example.map_proiect_extins.utils.observer.Observer;
import com.example.map_proiect_extins.validators.ServiceException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class MessageService implements Observable<MessageStatusEvent> {
    private MessageDataBaseRepository messageRepository;
    private Repository<Long, User> userRepository;

    public MessageService(MessageDataBaseRepository messageRepository, Repository<Long, User> userRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;

    }

    public void save(Long userIdFrom, String listReceivers, String messageText, Long replyId) {
        Long lastMessageId = messageRepository.findLastId();
        User from = userRepository.findOne(userIdFrom);
        if (from == null) {
            throw new ServiceException("No user with the " + userIdFrom + " id");
        }
        Message message = new Message(from, messageText);
        message.setId(lastMessageId + 1);


        String[] receivers = listReceivers.split(",");
        for (String idUser : receivers) {
            User receiverUser = userRepository.findOne(Long.parseLong(idUser));
            if (receiverUser == null) {
                throw new ServiceException("No receiver with the " + idUser + " id");
            }
            message.addUser(receiverUser);
        }
        messageRepository.save(message);

        notifyObservers(new MessageStatusEvent(ChangeEventType.ADD, message));

    }


    public void replyToAMessage(Long userIdFrom, String messageText, Long replyId) {
        Long lastMessageId = messageRepository.findLastId();
        User from = userRepository.findOne(userIdFrom);
        if (from == null) {
            throw new ServiceException("No user with the " + userIdFrom + " id");
        }
        Message message = new Message(from, messageText);
        message.setId(lastMessageId + 1);

        Message replyMessage = messageRepository.findOne(replyId);
        if (replyMessage == null) {
            throw new ServiceException("This message does not exist");
        }
        List<User> replyMessageToList = replyMessage.getTo();
        if (!replyMessage.getFrom().equals(from) && !replyMessageToList.contains(from)) {
            throw new ServiceException("User with id " + userIdFrom + " cannot reply to this message");
        }

        List<User> receiversUsers = new ArrayList<>(replyMessageToList);
        receiversUsers.removeIf(user -> user.getId().equals(userIdFrom));
        receiversUsers.add(replyMessage.getFrom());
        receiversUsers.forEach(user -> message.addUser(user));

        message.setReplyMessage(replyMessage);
        messageRepository.save(message);
    }

    public Iterable<Message> findConversation(Long userId1, Long userId2) {
        Iterable<Message> conversations = messageRepository.findConversation(userId1, userId2);
        if (conversations.spliterator().getExactSizeIfKnown() == 0)
            throw new ServiceException("There is no conversation between user " + userId1 + " and user " + userId2);
        List<Message> messageList = new ArrayList<>();
        conversations.forEach(message -> messageList.add(message));
        messageList.sort((m1, m2) -> m1.getData().compareTo(m2.getData()));
        return messageList;
    }

    private List<Observer<MessageStatusEvent>> observers = new ArrayList<>();

    @Override
    public void addObserver(Observer<MessageStatusEvent> e) {
        observers.add(e);
    }

    @Override
    public void removeObserver(Observer<MessageStatusEvent> e) {
        observers.remove(e);
    }

    @Override
    public void notifyObservers(MessageStatusEvent t) {
        observers.stream().forEach(x -> x.update(t));
    }


    private List<String> makeFilterString(List<Message> list, Long id) {
        return list.stream()
                .map(m -> new FilterMessagesDTO(m.getFrom(), m.getMessage(), m.getData()).toString())
                .toList();
    }


    private List<Message> filterMessagesWithDates(List<Message> list, Predicate<Message> p) {
        return list
                .stream()
                .filter(p)
                .sorted((m1, m2) -> m2.getData().compareTo(m1.getData()))
                .toList();
    }

    public List<String> raportMessagesInAPeriod(Long idUser, LocalDate DateS, LocalDate DateF) {
        if (userRepository.findOne(idUser) == null)
            throw new ServiceException("Invaid ID");
        if (DateS.isAfter(DateF))
            throw new ServiceException("Invaid dates");

        List<Message> list = new ArrayList<>();
        Predicate<Message> predicate = message -> (
                !message.getFrom().getId().equals(idUser)
                        && message.getData().toLocalDate().isAfter(DateS)
                        && message.getData().toLocalDate().isBefore(DateF));

        userRepository.findAll().forEach(usr -> {
            try {
                messageRepository.findConversation(idUser, usr.getId()).forEach(msg -> list.add(msg));
            } catch (Exception e) {
            }
        });
        return makeFilterString(filterMessagesWithDates(list, predicate), idUser);
    }


    public List<String> raportMessagesInAPeriodForAFriend(Long idUser, Long idFr, LocalDate DateS, LocalDate DateF) {
        if (userRepository.findOne(idUser) == null)
            throw new ServiceException("Invaid ID");
        if (DateS.isAfter(DateF))
            throw new ServiceException("Invaid dates");

        List<Message> list = new ArrayList<>();
        Predicate<Message> predicate = message -> (
                message.getFrom().getId().equals(idFr)
                        && message.getData().toLocalDate().isAfter(DateS)
                        && message.getData().toLocalDate().isBefore(DateF));

        try {
            messageRepository.findConversation(idUser, idFr).forEach(msg -> list.add(msg));
        } catch (Exception e) {
        }

        return makeFilterString(filterMessagesWithDates(list, predicate), idUser);
    }

}