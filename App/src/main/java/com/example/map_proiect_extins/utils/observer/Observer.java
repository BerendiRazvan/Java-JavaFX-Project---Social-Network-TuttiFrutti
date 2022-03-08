package com.example.map_proiect_extins.utils.observer;

import com.example.map_proiect_extins.utils.events.Event;

public interface Observer<E extends Event> {
    void update(E e);
}