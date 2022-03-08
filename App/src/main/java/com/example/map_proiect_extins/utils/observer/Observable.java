package com.example.map_proiect_extins.utils.observer;

import com.example.map_proiect_extins.utils.events.Event;

public interface Observable<E extends Event> {
    void addObserver(Observer<E> e);
    void removeObserver(Observer<E> e);
    void notifyObservers(E t);
}
