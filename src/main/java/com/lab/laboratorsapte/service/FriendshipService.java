package com.lab.laboratorsapte.service;

import com.lab.laboratorsapte.domain.Friendship;
import com.lab.laboratorsapte.domain.Tuple;
import com.lab.laboratorsapte.domain.User;
import com.lab.laboratorsapte.repository.DBFriendshipsRepo;
import com.lab.laboratorsapte.utils.events.UserTaskChangeEvent;
import com.lab.laboratorsapte.utils.observer.Observable;
import com.lab.laboratorsapte.utils.observer.Observer;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class FriendshipService implements Service<Tuple<Long,Long>, Friendship>, Observable<UserTaskChangeEvent> {

    DBFriendshipsRepo repo;
    private List<Observer<UserTaskChangeEvent>> observers=new ArrayList<>();

    public FriendshipService(DBFriendshipsRepo dbFriendshipsRepo) {
        this.repo = dbFriendshipsRepo;
    }

    @Override
    public Optional<Friendship> add(Friendship entity) {
        Long id1 = entity.getId().getLeft();
        Long id2 = entity.getId().getRight();

        Optional<User> user1O = repo.findOneUser(id1);
        Optional<User> user2O = repo.findOneUser(id2);

        if(user1O.isPresent() && user2O.isPresent()){
            return repo.save(entity);
        }else{
            throw new IllegalArgumentException("Nu exista user cu id-ul "+(user1O.isPresent() ? id2:id1));
        }
    }

    @Override
    public Optional<Friendship> delete(Tuple<Long, Long> longLongTuple) {
        Long id1 = longLongTuple.getLeft();
        Long id2 = longLongTuple.getRight();
        Optional<User> u1 = repo.findOneUser(id1);
        Optional<User> u2 = repo.findOneUser(id2);

        if(u1.isEmpty() || u2.isEmpty()){
            throw new IllegalArgumentException("User inexistent!");
        }

        Optional<Friendship> deleted = repo.delete(longLongTuple);
        return deleted;
    }

    @Override
    public Optional<Friendship> getEntityById(Tuple<Long, Long> longLongTuple) {
        return repo.findOne(longLongTuple);
    }

    @Override
    public Iterable<Friendship> getAll() {
        return repo.findAll();
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
