package com.example.map_proiect_extins.domain;

import java.util.Locale;

public class NotifDTO {
    Event event;
    String period;

    public NotifDTO(Event event, String period) {
        this.event = event;
        this.period = period;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    @Override
    public String toString() {
        return event.getName().toUpperCase(Locale.ROOT) + "\nIn " + period;
    }
}
