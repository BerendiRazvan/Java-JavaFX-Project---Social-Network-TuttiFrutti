package com.example.map_proiect_extins.utils.events;

import com.example.map_proiect_extins.domain.Event;

public class EventsStatusEvent implements com.example.map_proiect_extins.utils.events.Event {
    private ChangeEventType type;
    private Event data, oldData;

    public EventsStatusEvent(ChangeEventType type, Event data) {
        this.type = type;
        this.data = data;
    }
    public EventsStatusEvent(ChangeEventType type, Event data, Event oldData) {
        this.type = type;
        this.data = data;
        this.oldData=oldData;
    }

    public ChangeEventType getType() {
        return type;
    }

    public Event getData() {
        return data;
    }

    public Event getOldData() {
        return oldData;
    }

}
