package com.example.map_proiect_extins.domain;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class FilterMessagesDTO {
    private User sender;
    private String message;
    private LocalDateTime data;

    public FilterMessagesDTO(User sender, String message, LocalDateTime data) {
        this.sender = sender;
        this.message = message;
        this.data = data;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getData() {
        return data;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH-mm-ss");
        String formattedDateTime = data.format(formatter); // "1986-04-08 12:30:55"
        return formattedDateTime + ": " +
                message;
    }
}

