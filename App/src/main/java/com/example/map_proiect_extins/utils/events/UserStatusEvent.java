package com.example.map_proiect_extins.utils.events;

import com.example.map_proiect_extins.domain.User;

public class UserStatusEvent implements Event {

    private ChangeEventType type;
    private User data, oldData;

    public UserStatusEvent(ChangeEventType type, User data) {
        this.type = type;
        this.data = data;
    }
    public UserStatusEvent(ChangeEventType type, User data, User oldData) {
        this.type = type;
        this.data = data;
        this.oldData=oldData;
    }

    public ChangeEventType getType() {
        return type;
    }

    public User getData() {
        return data;
    }

    public User getOldData() {
        return oldData;
    }

}
