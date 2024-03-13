package com.lab.laboratorsapte.repository;

import com.lab.laboratorsapte.domain.User;
import com.lab.laboratorsapte.domain.validator.UserValidator;
import com.lab.laboratorsapte.repository.Paginat.Page;
import com.lab.laboratorsapte.repository.Paginat.Pageable;
import com.lab.laboratorsapte.repository.Paginat.PagingRepository;

import java.sql.*;
import java.util.*;

public class DBUsersRepo extends AbstractDBRepo<Long, User> implements PagingRepository<Long,User> {

    private final UserValidator validator;

    public DBUsersRepo(String url, String username, String password) {
        super(url, username, password);
        validator = new UserValidator();
    }


    @Override
    public Optional<User> findOne(Long id) {
        return findOneUser(id);
    }

    @Override
    public Iterable<User> findAll() {
        Set<User> users = new HashSet<>();

        try(Connection connection = DriverManager.getConnection(url,username,password);
            PreparedStatement statement = connection.prepareStatement("select * from users")){
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next())
            {
                Long id = resultSet.getLong("user_id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String email = resultSet.getString("email");
                String password = resultSet.getString("password");
                User u = new User(firstName, lastName, email, id, password);
                users.add(u);
            }
            return users;
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<User> save(User entity) {
        if(entity == null)
        {
            throw new IllegalArgumentException("Entitate nula!");
        }

        validator.validate(entity);

        if(findOne(entity.getId()).isPresent()){
            return Optional.of(entity);
        }

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            try (PreparedStatement statement = connection.prepareStatement("insert into users(user_id, first_name, last_name, email, password)" + "values(?,?,?,?,?)")) {
                statement.setInt(1, entity.getId().intValue());
                statement.setString(2, entity.getFirstname());
                statement.setString(3, entity.getLastname());
                statement.setString(4, entity.getEmail());
                statement.setString(5, entity.getPassword());
                int affectedrows = statement.executeUpdate();
                if (affectedrows == 0) {
                    return Optional.ofNullable(entity);
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<User> delete(Long id) {
        if(id == null)
        {
            throw new IllegalArgumentException("Id nu poate sa fie nul!");
        }

        Optional<User>entity = findOne(id);
        try(Connection connection = DriverManager.getConnection(url,username,password);
        PreparedStatement statement = connection.prepareStatement("DELETE FROM users WHERE user_id = ?")){
            statement.setInt(1,id.intValue());
            int affectedrows = statement.executeUpdate();
            if (affectedrows == 0) return Optional.empty();
            return Optional.ofNullable(entity.get());
        }catch(SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<User> update(User entity) {
        validator.validate(entity);
        try(Connection connection = DriverManager.getConnection(url,username,password);
        PreparedStatement statement = connection.prepareStatement("UPDATE users SET first_name = ?, last_name = ?, email = ? WHERE user_id = ?")){

            statement.setString(1,entity.getFirstname());
            statement.setString(2,entity.getLastname());
            statement.setString(3,entity.getEmail());
            statement.setInt(4, entity.getId().intValue());
            int affectedrows = statement.executeUpdate();
            if (affectedrows == 0) return Optional.empty();
            return Optional.ofNullable(entity);

        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public List<Long> getFriendsIds(Long id){

        List<Long> friends = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(url,username,password);
        PreparedStatement statement = connection.prepareStatement("select users.user_id, friendships.id1, friendships.id2 " +
                "from users " +
                "INNER JOIN friendships on (users.user_id = friendships.id1 or users.user_id = friendships.id2) " +
                "where users.user_id = ?")){

            statement.setInt(1, id.intValue());
            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next())
            {
                Long id1 = resultSet.getLong("id1");
                Long id2 = resultSet.getLong("id2");

                if(id1 != id) friends.add(id1);
                else friends.add(id2);
            }

        }catch (SQLException e){
            throw new RuntimeException(e);
        }
        return friends;
    }

    public List<Long> getFriendsIdsForFriendRequest(Long id){

        List<Long> idList = new ArrayList<>();

        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement(
                    "select users.user_id, frreq.id1, frreq.id2 " +
                            "from users " +
                            "INNER JOIN frreq on users.user_id = frreq.id2 " +
                            "where (users.user_id = ? and frreq.status = 'PENDING')");
        ){
            statement.setInt(1,id.intValue());
            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()){
                Long idBun = resultSet.getLong("id1");
                idList.add(idBun);
            }
        }
        catch(SQLException e){
            throw new RuntimeException(e);
        }

        return idList;
    }

    public Optional<User> findFirstByName(String fn, String ln) {

        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("select * from users " +
                    "where first_name = ? and last_name=? LIMIT 1");

        ){
            statement.setString(1,fn);
            statement.setString(2,ln);
            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next()){
                Integer integer = resultSet.getInt("user_id");
                Long id = integer.longValue();
                User user = new User(fn,ln);
                user.setId(id);
                return Optional.ofNullable(user);
            }
        }
        catch(SQLException e){
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }

    @Override
    public Page<User> findAllPaginat(Pageable pageable) {
        List<User> usersList = new ArrayList<>();
        try(Connection connection = DriverManager.getConnection(url,username,password);
        PreparedStatement pageStatement = connection.prepareStatement("SELECT * from users "+"LIMIT ? OFFSET ?");
        PreparedStatement countStatement = connection.prepareStatement("SELECT COUNT(*) AS count from users"))
        {
            pageStatement.setInt(1,pageable.getPageSize());
            pageStatement.setInt(2,pageable.getPageSize() * pageable.getPageNumber());
            try(ResultSet pageResultSet = pageStatement.executeQuery();
            ResultSet countResultSet = countStatement.executeQuery();)
            {
                while (pageResultSet.next())
                {
                    Long id = pageResultSet.getLong("user_id");
                    String nume = pageResultSet.getString("last_name");
                    String prenume = pageResultSet.getString("first_name");
                    String email = pageResultSet.getString("email");
                    String pass = pageResultSet.getString("password");
                    User u = new User(prenume,nume,email,id,pass);
                    usersList.add(u);
                }
                int totalCount = 0;
                if(countResultSet.next())
                {
                    totalCount = countResultSet.getInt("count");
                }
                return new Page<>(usersList,totalCount);
            }
        }catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }
}
