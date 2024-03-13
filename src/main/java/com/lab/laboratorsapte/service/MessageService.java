package com.lab.laboratorsapte.service;

import com.lab.laboratorsapte.domain.Message;
import com.lab.laboratorsapte.domain.User;
import com.lab.laboratorsapte.repository.DBMessagesRepo;
import com.lab.laboratorsapte.repository.DBUsersRepo;
import com.lab.laboratorsapte.utils.events.UserTaskChangeEvent;
import com.lab.laboratorsapte.utils.observer.Observable;
import com.lab.laboratorsapte.utils.observer.Observer;

import java.util.*;
import java.util.stream.Collectors;

public class MessageService implements Service<Long, Message>, Observable<UserTaskChangeEvent> {
    DBMessagesRepo repo;
    DBUsersRepo usersRepo;
    private List<Observer<UserTaskChangeEvent>> observers=new ArrayList<>();

    public MessageService(DBMessagesRepo repo, DBUsersRepo usersRepo) {
        this.repo = repo;
        this.usersRepo = usersRepo;
    }

    public ArrayList<Message> getMessagesBetweenTwoUsers(Long userId1, Long userId2) {
        if (usersRepo.findOne(userId1).isEmpty() || usersRepo.findOne(userId2).isEmpty())
            return new ArrayList<>();

        Collection<Message> messages = (Collection<Message>) repo.findAll();
        return messages.stream()
                .filter(x -> (x.getFrom().getId().equals(userId1) && x.getTo().get(0).getId().equals(userId2)) ||
                        (x.getFrom().getId().equals(userId2) && x.getTo().get(0).getId().equals(userId1)))
                .sorted(Comparator.comparing(Message::getDate))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public boolean addMessage(Long fromUserId, Long toUserId, String message) {
        try {
            User from = usersRepo.findOne(fromUserId).orElse(null);
            User to = usersRepo.findOne(toUserId).orElse(null);

            if (from == null || to == null)
                throw new Exception("Utilizatorul nu există");
            if (Objects.equals(message, ""))
                throw new Exception("Messageul este gol");

            Message msg = new Message(from, Collections.singletonList(to), message);
            repo.save(msg);

            List<Message> messagesTwoUsers = getMessagesBetweenTwoUsers(toUserId, fromUserId);
            if (messagesTwoUsers.size() > 1) {
                Message secondToLastMessage = messagesTwoUsers.get(messagesTwoUsers.size() - 2);
                Message lastMessage = messagesTwoUsers.get(messagesTwoUsers.size() - 1);
                lastMessage.setReply(secondToLastMessage);
                repo.update(lastMessage);
            }

            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    @Override
    public Optional<Message> add(Message entity) {
        if (addMessage(entity.getFrom().getId(), entity.getTo().get(0).getId(), entity.getMessage())) {
            return Optional.of(entity);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Message> delete(Long aLong) {
        return repo.delete(aLong);
    }

    @Override
    public Optional<Message> getEntityById(Long aLong) {
        return repo.findOne(aLong);
    }

    @Override
    public Iterable<Message> getAll() {
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
