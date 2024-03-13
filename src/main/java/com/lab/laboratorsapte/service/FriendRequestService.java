package com.lab.laboratorsapte.service;

import com.lab.laboratorsapte.domain.FriendRequest;
import com.lab.laboratorsapte.domain.FriendRequestStatus;
import com.lab.laboratorsapte.domain.Friendship;
import com.lab.laboratorsapte.domain.Tuple;
import com.lab.laboratorsapte.repository.DBFriendRequestsRepo;
import com.lab.laboratorsapte.repository.DBFriendshipsRepo;
import com.lab.laboratorsapte.repository.DBUsersRepo;
import com.lab.laboratorsapte.utils.events.UserTaskChangeEvent;
import com.lab.laboratorsapte.utils.observer.Observable;
import com.lab.laboratorsapte.utils.observer.Observer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FriendRequestService implements Service<Tuple<Long, Long>, FriendRequest>, Observable<UserTaskChangeEvent> {

    private DBFriendRequestsRepo repo;
    private DBUsersRepo dbUsersRepo;
    private DBFriendshipsRepo dbFriendshipsRepo;
    private List<Observer<UserTaskChangeEvent>> observers = new ArrayList<>();

    public FriendRequestService(DBFriendRequestsRepo repo, DBUsersRepo dbUsersRepo, DBFriendshipsRepo dbFriendshipsRepo) {
        this.repo = repo;
        this.dbUsersRepo = dbUsersRepo;
        this.dbFriendshipsRepo = dbFriendshipsRepo;
    }

    @Override
    public Optional<FriendRequest> add(FriendRequest entity) {
        Optional<Friendship> friendship = dbFriendshipsRepo.findOne(entity.getId());
        if(friendship.isPresent())
            throw new IllegalArgumentException("Eroare! Nu se poate trimite cererea, sunteti deja prieteni!");
        Optional<FriendRequest> fr = repo.findOne(entity.getId());
        if(fr.isPresent() && fr.get().getStatus()== FriendRequestStatus.PENDING)
            throw new IllegalArgumentException("Eroare! Cerere deja trimisa!");
        return repo.save(entity);
    }

    @Override
    public Optional<FriendRequest> delete(Tuple<Long, Long> longLongTuple) {
        return Optional.empty();
    }

    @Override
    public Optional<FriendRequest> getEntityById(Tuple<Long, Long> longLongTuple) {
        return repo.findOne(longLongTuple);
    }

    public List<Long> getFriendRequestIds(Long id){ return dbUsersRepo.getFriendsIdsForFriendRequest(id); }

    public Optional<FriendRequest> update(FriendRequest entity) { return repo.update(entity); }

    @Override
    public Iterable<FriendRequest> getAll() {
        return null;
    }

    @Override
    public void addObserver(Observer<UserTaskChangeEvent> e) {
        observers.add(e);
    }

    @Override
    public void removeObserver(Observer<UserTaskChangeEvent> e) {
        observers.remove(e);
    }

    @Override
    public void notifyObservers(UserTaskChangeEvent t) {
        observers.forEach(x->x.update(t));
    }
}
