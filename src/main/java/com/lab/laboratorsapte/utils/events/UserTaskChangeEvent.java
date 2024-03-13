package com.lab.laboratorsapte.utils.events;

import com.lab.laboratorsapte.domain.User;

public class UserTaskChangeEvent<E> implements Event {
    private ChangeEventType type;
    private E data, oldData;

    public UserTaskChangeEvent(ChangeEventType type, E data) {
        this.type = type;
        this.data = data;
    }
    public UserTaskChangeEvent(ChangeEventType type, E data,E oldData) {
        this.type = type;
        this.data = data;
        this.oldData=oldData;
    }

    public ChangeEventType getType() {
        return type;
    }

    public E getData() {
        return data;
    }

    public E getOldData() {
        return oldData;
    }
}