package com.example.map_proiect_extins.domain;

public class EventDTO {
    Event event;
    Boolean stat;//true = going; false = not going
    Boolean notif;//true = on; false = of
    String notifications;
    String status;


    public EventDTO(Event event, Boolean stat, Boolean notificari) {
        this.event = event;
        this.stat = stat;
        this.notif = notificari;

        if (notif)
            this.notifications = "On";
        else
            this.notifications = "Off";

        if (stat)
            this.status = "Going";
        else {
            this.status = "Not Going";
            this.notifications = "-";
        }
    }

    public String getNotifications() {
        return notifications;
    }

    public void setNotifications(String notifications) {
        this.notifications = notifications;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Boolean getStat() {
        return stat;
    }

    public void setStat(Boolean stat) {
        this.stat = stat;
    }

    public Boolean getNotif() {
        return notif;
    }

    public void setNotif(Boolean notif) {
        this.notif = notif;
    }
}
