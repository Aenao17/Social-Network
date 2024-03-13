package com.lab.laboratorsapte.repository;

import com.lab.laboratorsapte.domain.FriendRequest;
import com.lab.laboratorsapte.domain.FriendRequestStatus;
import com.lab.laboratorsapte.domain.Tuple;
import com.lab.laboratorsapte.domain.validator.FriendRequestValidator;

import java.sql.*;
import java.util.Optional;

public class DBFriendRequestsRepo extends AbstractDBRepo<Tuple<Long,Long>, FriendRequest> {

    private final FriendRequestValidator validator;

    public DBFriendRequestsRepo(String url, String username, String password){
        super(url,username,password);
        validator = new FriendRequestValidator();
    }

    @Override
    public Optional<FriendRequest> findOne(Tuple<Long, Long> longLongTuple) {
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("select * from frreq where (id1 = ? and id2 = ?)")){

            statement.setInt(1, Math.toIntExact(longLongTuple.getLeft()));
            statement.setInt(2, Math.toIntExact(longLongTuple.getRight()));

            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){
                String status = resultSet.getString("status");
                FriendRequestStatus friendRequestStatus;

                if(status.equals("PENDING"))
                    friendRequestStatus = FriendRequestStatus.PENDING;
                else if(status.equals("APPROVED"))
                    friendRequestStatus = FriendRequestStatus.APPROVED;
                else
                    friendRequestStatus = FriendRequestStatus.REJECTED;

                FriendRequest fr = new FriendRequest(longLongTuple.getLeft(),longLongTuple.getRight(),friendRequestStatus);
                return Optional.of(fr);
            }

        }catch(SQLException e){
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public Iterable<FriendRequest> findAll() {
        return null;
    }

    @Override
    public Optional<FriendRequest> save(FriendRequest entity) {
        if(entity.getId() == null)
            throw new IllegalArgumentException("Eroare! ID-urile nu pot sa fie null!");
        validator.validate(entity);

        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("insert into frreq(id1, id2, status) " +
                    "values(?,?,?)");
        ){
            statement.setInt(1,entity.getId().getLeft().intValue());
            statement.setInt(2,entity.getId().getRight().intValue());
            statement.setString(3,entity.getStatus().toString());

            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) return Optional.of(entity);
            return Optional.empty();
        }
        catch(SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<FriendRequest> delete(Tuple<Long, Long> longLongTuple) {
        return Optional.empty();
    }

    @Override
    public Optional<FriendRequest> update(FriendRequest entity) {
        if(entity.getId() == null)
            throw new IllegalArgumentException("Eroare! ID-urile nu pot sa fie null!");
        validator.validate(entity);

        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("update frreq set status = ? " +
                    "where (id1 = ? and id2 = ?)");
        ){
            statement.setString(1, entity.getStatus().toString());
            statement.setInt(2,entity.getId().getLeft().intValue());
            statement.setInt(3,entity.getId().getRight().intValue());
            int affectedRows = statement.executeUpdate();
            return affectedRows == 0 ? Optional.ofNullable(entity) : Optional.empty();
        }
        catch(SQLException e){
            throw new RuntimeException(e);
        }
    }
}
