package com.example.map_proiect_extins.utils.events;

import com.example.map_proiect_extins.domain.Friendship;
import com.example.map_proiect_extins.domain.Message;

public class FriendshipStatusEvent implements Event {
    private ChangeEventType type;
    private Friendship data, oldData;

    public FriendshipStatusEvent(ChangeEventType type, Friendship data) {
        this.type = type;
        this.data = data;
    }
    public FriendshipStatusEvent(ChangeEventType type, Friendship data, Friendship oldData) {
        this.type = type;
        this.data = data;
        this.oldData=oldData;
    }

    public ChangeEventType getType() {
        return type;
    }

    public Friendship getData() {
        return data;
    }

    public Friendship getOldData() {
        return oldData;
    }
}
