package com.lab.laboratorsapte.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class User extends Entity<Long> {
    private String firstname;
    private String lastname;
    private String email;
    private final ArrayList<User> friends;
    private String password;

    public User(String firstname, String lastname, String email, String password) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
        friends = new ArrayList<>();
    }

    public User(String firstname, String lastname, String email, Long id, String pass) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        friends = new ArrayList<>();
        this.setId(id);
        this.password = pass;
    }

    public User(String fn, String ln) {
        this.firstname = fn;
        this.lastname = ln;
        friends = new ArrayList<>();
    }

    public User(Long id, String fn, String ln) {
        this.firstname = fn;
        this.lastname = ln;
        friends = new ArrayList<>();
        this.setId(id);
    }



    public void addFriend (User u)
    {
        friends.add(u);
    }

    public void removeFriend (User u)
    {
        friends.remove(u);
    }
    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<User> getFriends() {
        return friends;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        User user = (User) o;
        return Objects.equals(firstname, user.firstname) && Objects.equals(lastname, user.lastname) && Objects.equals(email, user.email) && Objects.equals(friends, user.friends);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), firstname, lastname, email, friends);
    }

    @Override
    public String toString() {
        return "User{" +
                "firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
