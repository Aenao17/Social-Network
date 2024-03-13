package com.lab.laboratorsapte.domain;

import java.time.LocalDateTime;
import java.util.Objects;

public class Friendship extends Entity<Tuple<Long,Long>> {
    private LocalDateTime friendsFrom;

    public Friendship(LocalDateTime friendsFrom) {
        this.friendsFrom = friendsFrom;
    }

    public Friendship(Long u1, Long u2, LocalDateTime timp){
        this.friendsFrom = timp;
        this.setId(new Tuple(u1,u2));
    }

    public LocalDateTime getFriendsFrom() {
        return friendsFrom;
    }

    public void setFriendsFrom(LocalDateTime friendsFrom) {
        this.friendsFrom = friendsFrom;
    }
}
