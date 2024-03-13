package com.lab.laboratorsapte.service;

import com.lab.laboratorsapte.domain.User;
import com.lab.laboratorsapte.repository.DBUsersRepo;
import com.lab.laboratorsapte.repository.Paginat.Page;
import com.lab.laboratorsapte.repository.Paginat.Pageable;
import com.lab.laboratorsapte.utils.events.UserTaskChangeEvent;
import com.lab.laboratorsapte.utils.observer.Observable;
import com.lab.laboratorsapte.utils.observer.Observer;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class UserService implements Service<Long, User>, Observable<UserTaskChangeEvent> {

    private DBUsersRepo repo;
    private List<Observer<UserTaskChangeEvent>> observers = new ArrayList<>();

    public UserService(DBUsersRepo repo) {
        this.repo = repo;
    }

    @Override
    public Optional<User> add(User entity) {
        return repo.save(entity);
    }

    @Override
    public Optional<User> delete(Long aLong) {
        return repo.delete(aLong);
    }

    public Optional<User> update(User u) {
        return repo.update(u);
    }

    @Override
    public Optional<User> getEntityById(Long aLong) {
        return repo.findOne(aLong);
    }

    @Override
    public Iterable<User> getAll() {
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
        observers.forEach(o->o.update(t));
    }

    private void DFS(User User, Map<User, Integer> visited, int len) {
        visited.put(User, len);
        List<Long> list = repo.getFriendsIds(User.getId());
        list.forEach(userId -> {
            if (!visited.containsKey(repo.findOne(userId).get()) || visited.get(repo.findOne(userId).get()) == 0) {
                DFS(repo.findOne(userId).get(), visited, len);
            }
        });
    }

    public int nrComunitati() {

        Map<User, Integer> visited = new HashMap<>();
        AtomicInteger numar_comunitati = new AtomicInteger(0);

        getAll().forEach(user -> {
            if (!visited.containsKey(user) || visited.get(user) == 0) {
                numar_comunitati.incrementAndGet();
                DFS(user, visited, numar_comunitati.get());
            }
        });
        return numar_comunitati.get();
    }

    private int BFS(User User, Map<User, Integer> visited) {
        int maxim = -1;
        Queue<User> coada = new LinkedList<>();
        coada.add(User);
        visited.put(User, 1);

        while (!coada.isEmpty()) {
            User nextUser = coada.peek();
            coada.poll();
            for (Long userId : repo.getFriendsIds(nextUser.getId())) {
                if (!visited.containsKey(repo.findOne(userId).get()) || visited.get(repo.findOne(userId).get()) == 0) {
                    int nxt_value = visited.get(nextUser) + 1;

                    if (nxt_value > maxim) maxim = nxt_value;

                    visited.put(repo.findOne(userId).get(), nxt_value);
                    coada.add(repo.findOne(userId).get());
                }
            }
        }
        return maxim;
    }

    private void populeaza(User User, List<User> list) {
        List<Long> friendsId = repo.getFriendsIds(User.getId());
        friendsId.forEach(userId -> {
            if (!list.contains(repo.findOne(userId).get())) {
                list.add(repo.findOne(userId).get());
                populeaza(repo.findOne(userId).get(), list);
            }
        });
    }

    public List<User> comunitateaSociabila() {
        Map<User, Integer> visited = new HashMap<>();
        List<User> result = new ArrayList<>();
        int maxim = -1;

        for (User user : getAll())
            if (!visited.containsKey(user) || visited.get(user) == 0) {
                int lung = BFS(user, visited);
                if (lung > maxim) {
                    maxim = lung;
                    if (!result.isEmpty()) result.clear();
                    populeaza(user, result);
                }
            }

        return result;
    }

    public Optional<User> findUserByName(String fn, String ln)
    {
        return repo.findFirstByName(fn,ln);
    }
    public List<Long> get_friends(Long id)
    {
        return  repo.getFriendsIds(id);
    }

    public void tryLogin(String id, String pass){
        Optional<User> u = getEntityById(Long.parseLong(id));
        if(u.isPresent()){
            if(u.get().getPassword().equals(encrypt(pass)))
            {

            }else{
                throw new RuntimeException("Parola incorecta!");
            }
        }else{
            throw new RuntimeException("Nu existad niciul user cu acest id!");
        }
    }

    public String encrypt(String pass){
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        md.update(pass.getBytes());
        byte [] digest = md.digest();
        StringBuffer hexString = new StringBuffer();
        for (int i=0;i<digest.length;i++){
            hexString.append(Integer.toHexString(0xFF & digest[i]));
        }
        return hexString.toString();
    }

    public Page<User> findAllPAGINAT(Pageable pageable)
    {
        return repo.findAllPaginat(pageable);
    }
}
