package com.lab.laboratorsapte.utils.observer;

import com.lab.laboratorsapte.utils.events.Event;

public interface Observer<E extends Event> {
    void update(E e);
}
