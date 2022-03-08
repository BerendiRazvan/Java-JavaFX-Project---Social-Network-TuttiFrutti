package com.example.map_proiect_extins.domain;

public class EventParticipant extends Entity<Pair<Long>> {

    Boolean notifications;

    public EventParticipant(Long idUser, Long idEvent) {
        setId(new Pair<>(idUser, idEvent));
        notifications = true;
    }

    public Boolean getNotifications() {
        return notifications;
    }

    public void setNotifications(Boolean notifications) {
        this.notifications = notifications;
    }

    @Override
    public String toString() {
        return "EventParticipant{ ID User:" + getId().getFirst() +
                "; ID Event:" + getId().getSecond() +
                "; notifications=" + notifications +
                '}';
    }
}
