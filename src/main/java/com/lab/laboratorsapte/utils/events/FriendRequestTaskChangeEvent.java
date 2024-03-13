package com.lab.laboratorsapte.utils.events;

import com.lab.laboratorsapte.domain.FriendRequest;

public class FriendRequestTaskChangeEvent extends UserTaskChangeEvent<FriendRequest>{
    private ChangeEventType type;
    private FriendRequest data, oldData;

    public FriendRequestTaskChangeEvent(ChangeEventType type, FriendRequest data) {
        super(type, data);
    }
    public FriendRequestTaskChangeEvent(ChangeEventType type, FriendRequest data, FriendRequest oldData) {
        super(type, data, oldData);
    }
}
