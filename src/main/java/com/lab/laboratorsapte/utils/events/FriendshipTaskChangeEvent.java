package com.lab.laboratorsapte.utils.events;

import com.lab.laboratorsapte.domain.FriendRequest;
import com.lab.laboratorsapte.domain.Friendship;

public class FriendshipTaskChangeEvent extends UserTaskChangeEvent<Friendship> {

    private ChangeEventType type;
    private Friendship data, oldData;
    public FriendshipTaskChangeEvent(ChangeEventType type, Friendship data){ super(type, data);}

}
