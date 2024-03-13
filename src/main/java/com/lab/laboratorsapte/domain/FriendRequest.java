package com.lab.laboratorsapte.domain;

import java.util.Objects;

public class FriendRequest extends Entity<Tuple<Long,Long>> {
    private FriendRequestStatus status;

    public FriendRequest(FriendRequestStatus status) {
        this.status = status;
    }

    public FriendRequest(Long id1, Long id2, FriendRequestStatus status){
        this.status = status;
        this.setId(new Tuple(id1,id2));
    }

    public FriendRequestStatus getStatus() {
        return status;
    }

    public void setStatus(FriendRequestStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        FriendRequest that = (FriendRequest) o;
        return status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), status);
    }

    @Override
    public String toString() {
        return "FriendRequest{" +
                "status=" + status +
                ", id=" + id +
                '}';
    }
}
