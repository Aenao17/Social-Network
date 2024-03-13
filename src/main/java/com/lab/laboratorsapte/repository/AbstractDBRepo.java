package com.lab.laboratorsapte.repository;

import com.lab.laboratorsapte.domain.Entity;
import com.lab.laboratorsapte.domain.User;

import java.sql.*;
import java.util.*;

public abstract class AbstractDBRepo <ID,E extends Entity<ID>> implements Repository <ID,E> {
    protected String url;
    protected String username;
    protected String password;

    public AbstractDBRepo(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public Optional<User> findOneUser(Long id) {
        if(id == null){
            throw new IllegalArgumentException("Id nu poate sa fie null!");
        }

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            try (PreparedStatement statement = connection.prepareStatement("select * from users where user_id=?")) {
                statement.setInt(1, Math.toIntExact( id));
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    String firstName = resultSet.getString("first_name");
                    String last_name = resultSet.getString("last_name");
                    String email = resultSet.getString("email");
                    String pass = resultSet.getString("password");
                    User user = new User(firstName, last_name, email, id, pass);
                    return Optional.ofNullable(user);
                }

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }
}
