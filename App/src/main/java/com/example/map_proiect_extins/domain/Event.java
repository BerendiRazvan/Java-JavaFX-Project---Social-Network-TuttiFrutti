package com.example.map_proiect_extins.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Event extends Entity<Long> {
    private String name;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime finishDate;
    private User organiser;

    public Event(String name, String description, LocalDateTime startDate, LocalDateTime finishDate, User organiser) {
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.finishDate = finishDate;
        this.organiser = organiser;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(LocalDateTime finishDate) {
        this.finishDate = finishDate;
    }

    public User getOrganiser() {
        return organiser;
    }

    public void setOrganiser(User organiser) {
        this.organiser = organiser;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String formattedDateTimeStart = startDate.format(formatter); // "1986-04-08 12:30:55"
        String formattedDateTimeFinish = finishDate.format(formatter); // "1986-04-08 12:30:55"
        return  name.toUpperCase(Locale.ROOT) +
                "\n" + description +
                "\nFrom: " + formattedDateTimeStart + "\nUntil:" + formattedDateTimeFinish +
                "\nOrganised by: " + organiser.getFirstName() + " " + organiser.getLastName() + "\n";
    }
}
