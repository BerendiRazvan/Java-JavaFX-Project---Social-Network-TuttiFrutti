package com.example.map_proiect_extins.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class User extends Entity<Long> {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private List<User> friends;

    public User(String firstName, String lastName, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        friends = new ArrayList<>();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void addFriend(User user) {
        friends.add(user);
    }

    public void removeFriend(User user) {
        friends.remove(user);
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

    public List<User> getFriends() {
        return friends;
    }

    @Override
    public String toString() {
        StringBuilder listOfFriends = new StringBuilder("[");
        for (User user : friends)
            listOfFriends.append(user.getFirstName()).append(" ").append(user.getLastName()).append(",");
        if (friends.size() != 0)
            listOfFriends.deleteCharAt(listOfFriends.length() - 1);
        listOfFriends.append("]");
        return "Utilizator{" +
                "id='" + super.id + '\'' +
                "firstName='" + firstName + '\'' +
                " lastName='" + lastName + '\'' +
                " email='" + email + '\'' +
                " password='" + password + '\'' +
                " friends=" + listOfFriends +
                '}' + "\n";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User that = (User) o;
        return getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

}