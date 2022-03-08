package com.example.map_proiect_extins.domain;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


public class FilterFriendshipsDTO {

    private String firstName;
    private String lastName;
    private LocalDate dateFriendship;

    public FilterFriendshipsDTO(String firstName, String lastName, LocalDate dateFriendship) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateFriendship = dateFriendship;
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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String formattedDateTime = dateFriendship.format(formatter); // "1986-04-08 12:30"
        return  formattedDateTime + ": "+
                firstName + " " +
                lastName;
    }

}
