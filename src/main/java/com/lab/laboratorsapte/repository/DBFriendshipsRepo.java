package com.lab.laboratorsapte.repository;

import com.lab.laboratorsapte.domain.Friendship;
import com.lab.laboratorsapte.domain.Tuple;
import com.lab.laboratorsapte.domain.validator.FriendshipValidator;

import java.io.FileReader;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;

public class DBFriendshipsRepo extends AbstractDBRepo<Tuple<Long,Long>, Friendship> {
    private final FriendshipValidator validator;

    public DBFriendshipsRepo(String url, String username, String password) {
        super(url, username, password);
        validator = new FriendshipValidator();
    }

    @Override
    public Optional<Friendship> findOne(Tuple<Long, Long> longLongTuple) {
        try(Connection connection = DriverManager.getConnection(url,username,password);
            PreparedStatement statement = connection.prepareStatement("select * from friendships where (id1 = ? and id2 = ?) or (id1 = ? and id2 =? )")){

            statement.setInt(1, Math.toIntExact(longLongTuple.getLeft()));
            statement.setInt(2, Math.toIntExact(longLongTuple.getRight()));
            statement.setInt(4, Math.toIntExact(longLongTuple.getLeft()));
            statement.setInt(3, Math.toIntExact(longLongTuple.getRight()));

            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){
                Long id1 = resultSet.getLong("id1");
                Long id2 = resultSet.getLong("id2");
                LocalDateTime data = resultSet.getTimestamp(3).toLocalDateTime();
                Friendship fr = new Friendship(id1,id2,data);
                return Optional.ofNullable(fr);
            }

        }catch(SQLException e){
            throw  new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public Iterable<Friendship> findAll() {
        Set<Friendship> frs = new HashSet<>();

        try(Connection connection = DriverManager.getConnection(url, username, password);
        PreparedStatement statement = connection.prepareStatement("select * from friendships");
        ResultSet resultSet = statement.executeQuery()){

            while(resultSet.next())
            {
                Long id1 = resultSet.getLong("id1");
                Long id2 = resultSet.getLong("id2");
                LocalDateTime data = resultSet.getTimestamp(3).toLocalDateTime();
                Friendship fr = new Friendship(id1,id2,data);
                frs.add(fr);
            }
            return frs;

        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Friendship> save(Friendship entity) {

        if(entity == null)
        {
            throw new IllegalArgumentException("Entitate nula!");
        }

        validator.validate(entity);

        try(Connection connection = DriverManager.getConnection(url, username, password);
        PreparedStatement statement = connection.prepareStatement("insert into friendships(id1, id2, timp) values(?,?,?)")){

            statement.setInt(1, entity.getId().getLeft().intValue());
            statement.setInt(2, entity.getId().getRight().intValue());
            statement.setTimestamp(3, Timestamp.valueOf(entity.getFriendsFrom()));

            int rows = statement.executeUpdate();
            if(rows == 0) return Optional.ofNullable(entity);
            return Optional.empty();

        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Friendship> delete(Tuple<Long, Long> longLongTuple) {
        if(longLongTuple == null)
            throw new IllegalArgumentException("Pereche de id nula!");

        try(Connection connection = DriverManager.getConnection(url, username, password);
        PreparedStatement statement = connection.prepareStatement("delete from friendships where (id1 = ? AND id2 = ?) OR (id1 = ? AND id2 = ?)")){

            Optional<Friendship> fr = findOne(longLongTuple);

            statement.setInt(1,longLongTuple.getLeft().intValue());
            statement.setInt(2,longLongTuple.getRight().intValue());
            statement.setInt(4,longLongTuple.getLeft().intValue());
            statement.setInt(3,longLongTuple.getRight().intValue());

            int rows = statement.executeUpdate();
            if(rows == 0) return Optional.ofNullable(fr.get());
                return Optional.empty();

        }catch(SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Friendship> update(Friendship entity) {
        if(entity == null)
        {
            throw new IllegalArgumentException("Entitate nula!");
        }

        validator.validate(entity);

        try(Connection connection = DriverManager.getConnection(url, username, password);
        PreparedStatement statement = connection.prepareStatement("update friendships set timp=? where (id1 = ? and id2 = ?) or (id1 = ? and id2 = ?)")){

            statement.setTimestamp(1,Timestamp.valueOf(entity.getFriendsFrom()));
            statement.setInt(2, entity.getId().getLeft().intValue());
            statement.setInt(3, entity.getId().getRight().intValue());
            statement.setInt(5, entity.getId().getLeft().intValue());
            statement.setInt(4, entity.getId().getRight().intValue());

            int rows = statement.executeUpdate();
            if(rows == 0) return Optional.ofNullable(entity);
                return Optional.empty();

        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
