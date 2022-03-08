package com.example.map_proiect_extins.domain;

import java.time.LocalDate;

public class UserFriendsDTO {
    private String firstName;
    private String lastName;
    private LocalDate dateFriendship;
    private Pair<Long> id;

    public UserFriendsDTO(String firstName, String lastName, LocalDate dateFriendship, Pair<Long> id) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateFriendship = dateFriendship;
        this.id = id;
    }

    public Pair<Long> getId() {
        return id;
    }

    public void setId(Pair<Long> id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getDateFriendship() {
        return dateFriendship;
    }

    public void setDateFriendship(LocalDate dateFriendship) {
        this.dateFriendship = dateFriendship;
    }

    @Override
    public String toString() {
        return firstName + " " + lastName;
    }
}
