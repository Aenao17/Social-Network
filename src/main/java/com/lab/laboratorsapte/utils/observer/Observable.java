package com.lab.laboratorsapte.utils.observer;

import com.lab.laboratorsapte.utils.events.Event;

public interface Observable<E extends Event> {
    void addObserver(Observer<E> e);
    void removeObserver(Observer<E> e);
    void notifyObservers(E t);
}
