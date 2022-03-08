package com.example.map_proiect_extins.domain;

import com.example.map_proiect_extins.utils.Constants;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Message extends Entity<Long> {
    private User from;
    private List<User> to;
    private String message;
    private LocalDateTime data;
    private Message reply;

    public Message(User from, String message) {
        this.from = from;
        this.to = new ArrayList<>();
        this.message = message;
        this.data = LocalDateTime.now();
        this.reply=null;
    }

    public void addUser(User user) {
        to.add(user);
    }

    public void removeUser(User user) {
        to.remove(user);
    }

    public LocalDateTime getData() {
        return data;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }


    public Message getReplyMessage() {
        return reply;
    }

    public void setReplyMessage(Message reply) {
        this.reply = reply;
    }

    public void setTo(List<User> to) {
        this.to = to;
    }

    public User getFrom() {
        return from;
    }

    public void setFrom(User from) {
        this.from = from;
    }

    public List<User> getTo() {
        return to;
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    @Override
    public String toString() {
        StringBuilder listOfUsers = new StringBuilder("[");
        for (User user : to)
            listOfUsers.append(user.getFirstName()).append(" ").append(user.getLastName()).append(",");
        if(to.size()!=0)
            listOfUsers.deleteCharAt(listOfUsers.length()-1);
        listOfUsers.append("]");
        if(reply!=null) {
            return "Message{" +
                    "id=" + id +
                    ", from=" + from.getFirstName() + " " + from.getLastName() +
                    ", to=" + listOfUsers +
                    ", data=" + data.format(Constants.DATE_TIME_FORMATTER) +
                    ", reply to message:" + reply.getId() + ", " + reply.getMessage() +
                    ",with message='" + message + '\'' +
                    '}' + "\n";
        }else{
            return "Message{" +
                    "id=" + id +
                    ", from=" + from.getFirstName() + " " + from.getLastName() +
                    ", to=" + listOfUsers +
                    ", message='" + message + '\'' +
                    ", data=" + data.format(Constants.DATE_TIME_FORMATTER) + '}' + "\n";
        }
    }

}
