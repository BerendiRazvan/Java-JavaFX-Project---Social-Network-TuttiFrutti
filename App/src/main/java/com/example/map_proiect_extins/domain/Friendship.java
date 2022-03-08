package com.example.map_proiect_extins.domain;

import java.time.LocalDate;

public class Friendship extends Entity<Pair<Long>> { //deja le am in pair(id1,id2)
    LocalDate date;
    String status;

    public Friendship(Pair<Long> id) {
        this.id = id;
        this.date = LocalDate.now();
        this.status = "pending";
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Friendship{" +
                "id1=" + super.id.getFirst() + " " +
                "id2=" + super.id.getSecond() +
                ", date=" + date +
                ", status=" + status +
                '}' + "\n";
    }

}

