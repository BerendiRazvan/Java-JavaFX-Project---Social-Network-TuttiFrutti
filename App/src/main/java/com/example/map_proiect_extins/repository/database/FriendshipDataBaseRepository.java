package com.example.map_proiect_extins.repository.database;

import com.example.map_proiect_extins.domain.Friendship;
import com.example.map_proiect_extins.domain.Pair;
import com.example.map_proiect_extins.domain.User;
import com.example.map_proiect_extins.repository.Repository;
import com.example.map_proiect_extins.repository.paging.Page;
import com.example.map_proiect_extins.repository.paging.Pageable;
import com.example.map_proiect_extins.repository.paging.Paginator;
import com.example.map_proiect_extins.repository.paging.PagingRepository;
import com.example.map_proiect_extins.validators.Validator;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public class FriendshipDataBaseRepository implements PagingRepository<Pair<Long>, Friendship> {
    private String url;
    private String username;
    private String password;
    private Validator<Friendship> validator;

    public FriendshipDataBaseRepository(String url, String username, String password, Validator<Friendship> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }


    @Override
    public Friendship findOne(Pair<Long> longPair) {
        if (longPair == null)
            throw new IllegalArgumentException("id must be not null");

        String sql = "select * from friendships where id1 = ? AND id2=?";
        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setLong(1, longPair.getFirst());
            ps.setLong(2, longPair.getSecond());
            ResultSet resultSet = ps.executeQuery();

            while (resultSet.next()) {
                Long id1 = resultSet.getLong("id1");
                Long id2 = resultSet.getLong("id2");
                LocalDate date = resultSet.getObject(3, LocalDate.class);
                Friendship friendship = new Friendship(new Pair<>(id1, id2));
                friendship.setDate(date);
                String status = resultSet.getString("status");
                friendship.setStatus(status);
                return friendship;

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Iterable<Friendship> findAll() {
        Set<Friendship> friendships = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from friendships where status = 'approved'");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id1 = resultSet.getLong("id1");
                Long id2 = resultSet.getLong("id2");
                LocalDate date = resultSet.getObject(3, LocalDate.class);
                Friendship friendship = new Friendship(new Pair<>(id1, id2));
                friendship.setDate(date);
                String status = resultSet.getString("status");
                friendship.setStatus(status);
                friendships.add(friendship);
            }
            return friendships;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return friendships;
    }

    public Iterable<Friendship> findAllRequests() {
        Set<Friendship> friendships = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from friendships");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id1 = resultSet.getLong("id1");
                Long id2 = resultSet.getLong("id2");
                LocalDate date = resultSet.getObject(3, LocalDate.class);
                Friendship friendship = new Friendship(new Pair<>(id1, id2));
                friendship.setDate(date);
                String status = resultSet.getString("status");
                friendship.setStatus(status);
                friendships.add(friendship);
            }
            return friendships;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return friendships;
    }

    @Override
    public Friendship save(Friendship entity) {
        if (entity == null)
            throw new IllegalArgumentException("entity must be not null");
        validator.validate(entity);
        String sql = "insert into friendships (id1, id2, friendship_date, status) values (?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, entity.getId().getFirst());
            ps.setLong(2, entity.getId().getSecond());
            ps.setDate(3, Date.valueOf(entity.getDate()));
            ps.setString(4, entity.getStatus());

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Friendship delete(Pair<Long> longPair) {
        if (longPair == null)
            throw new IllegalArgumentException("id must be not null");

        Friendship friendship = findOne(longPair);
        String sql = "delete from friendships where id1 = ? AND id2 = ?";
        int rowCount = 0;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, longPair.getFirst());
            ps.setLong(2, longPair.getSecond());
            rowCount = ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (rowCount > 0) {
            return friendship;
        }

        return null;
    }

    @Override
    public Friendship update(Friendship entity) {
        if (entity.getId().getFirst() == null || entity.getId().getSecond() == null)
            throw new IllegalArgumentException("IDs must be not null");

        validator.validate(entity);

        if (findOne(entity.getId()) == null)
            return entity;
        else {
            String sql = "update friendships set friendship_date = ?, status = ? where id1 = ? and id2 = ?";
            try (Connection connection = DriverManager.getConnection(url, username, password);
                 PreparedStatement ps = connection.prepareStatement(sql)) {

                Date dataFriendship = Date.valueOf(entity.getDate().toString());
                ps.setDate(1, dataFriendship);

                ps.setString(2, entity.getStatus());

                ps.setLong(3, entity.getId().getFirst());
                ps.setLong(4, entity.getId().getSecond());

                ps.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    public Set<Friendship> findUserFriends(Long currentUserId) {
        Set<Friendship> friendships = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from friendships where status = 'approved' " +
                     "and (friendships.id1=? or friendships.id2=?)");) {
            statement.setLong(1,currentUserId);
            statement.setLong(2,currentUserId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Long id1 = resultSet.getLong("id1");
                Long id2 = resultSet.getLong("id2");
                LocalDate date = resultSet.getObject(3, LocalDate.class);
                Friendship friendship = new Friendship(new Pair<>(id1, id2));
                friendship.setDate(date);
                String status = resultSet.getString("status");
                friendship.setStatus(status);
                friendships.add(friendship);
            }
            return friendships;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return friendships;
    }

    @Override
    public Page<Friendship> findAllPaginated(Pageable pageable, Long currentUserId) {
        Set<Friendship> userFriends = findUserFriends(currentUserId);
        Paginator<Friendship> paginator = new Paginator<Friendship>(pageable, userFriends);
        return paginator.paginate();
    }
}
