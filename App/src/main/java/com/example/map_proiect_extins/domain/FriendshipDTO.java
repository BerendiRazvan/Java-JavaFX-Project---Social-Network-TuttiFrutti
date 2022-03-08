package com.example.map_proiect_extins.domain;

import java.time.LocalDate;

public class FriendshipDTO {
    Pair<Long> id;
    LocalDate date;
    String status;
    String friendFirstName;
    String friendLastName;

    public FriendshipDTO(Pair<Long> id, LocalDate date, String status, String friendFirstName, String friendLastName) {
        this.id = id;
        this.friendFirstName = friendFirstName;
        this.friendLastName = friendLastName;
        this.date = date;
        this.status = status;
    }

    public Pair<Long> getId() {
        return id;
    }

    public void setId(Pair<Long> id) {
        this.id = id;
    }

    public String getFriendFirstName() {
        return friendFirstName;
    }

    public void setFriendFirstName(String friendFirstName) {
        this.friendFirstName = friendFirstName;
    }

    public String getFriendLastName() {
        return friendLastName;
    }

    public void setFriendLastName(String friendLastName) {
        this.friendLastName = friendLastName;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
