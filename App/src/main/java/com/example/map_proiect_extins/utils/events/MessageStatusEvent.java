package com.example.map_proiect_extins.utils.events;


import com.example.map_proiect_extins.domain.Message;

public class MessageStatusEvent implements Event {
    private ChangeEventType type;
    private Message data, oldData;

    public MessageStatusEvent(ChangeEventType type, Message data) {
        this.type = type;
        this.data = data;
    }
    public MessageStatusEvent(ChangeEventType type, Message data, Message oldData) {
        this.type = type;
        this.data = data;
        this.oldData=oldData;
    }

    public ChangeEventType getType() {
        return type;
    }

    public Message getData() {
        return data;
    }

    public Message getOldData() {
        return oldData;
    }
}