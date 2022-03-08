package com.example.map_proiect_extins.repository.database;

import com.example.map_proiect_extins.domain.User;
import com.example.map_proiect_extins.repository.Repository;
import com.example.map_proiect_extins.repository.paging.Page;
import com.example.map_proiect_extins.repository.paging.Pageable;
import com.example.map_proiect_extins.repository.paging.Paginator;
import com.example.map_proiect_extins.repository.paging.PagingRepository;
import com.example.map_proiect_extins.utils.Encrypt;
import com.example.map_proiect_extins.validators.Validator;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

//nu mai tinem in memorie sau in fisiere, implementam direct interfata
public class UserDataBaseRepository implements PagingRepository<Long, User> {
    private String url;
    private String username;
    private String password;
    private Validator<User> validator;

    public UserDataBaseRepository(String url, String username, String password, Validator<User> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }

    @Override
    public User findOne(Long aLong) {
        if (aLong == null)
            throw new IllegalArgumentException("id must be not null");

        String sql = "select id,first_name, last_name, email, password from users where id = ?";
        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setLong(1, aLong);
            ResultSet resultSet = ps.executeQuery();

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String email = resultSet.getString("email");
                String password = resultSet.getString("password");

                User user = new User(firstName, lastName, email, decryptPassword(password));
                user.setId(id);
                return user;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public Iterable<User> findAll() {
        Set<User> users = new HashSet<>();
        String sql = "SELECT * from users left join friendships on users.id = friendships.id1 or users.id = friendships.id2";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                long id = resultSet.getLong("id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String email = resultSet.getString("email");
                String password = resultSet.getString("password");

                long id1 = resultSet.getLong("id1");
                long id2 = resultSet.getLong("id2");

                User user = new User(firstName, lastName, email, decryptPassword(password));
                user.setId(id);
//                if (id1 != 0 && id2 != 0) {
//                    Long friendId = id == id1 ? id2 : id1;
//                    if (users.contains(user)) {
//                        users
//                                .stream()
//                                .filter(user::equals)
//                                .findAny()
//                                .ifPresent(user1 -> user1.addFriend(findOne(friendId)));
//                    } else {
//                        user.addFriend(findOne(friendId));
//                    }
//                }
                users.add(user);
            }
            return users;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    @Override
    public User save(User entity) {
        if (entity == null)
            throw new IllegalArgumentException("entity must be not null");
        validator.validate(entity);

        String sql = "insert into users (id,first_name, last_name, email, password ) values (?,?,?,?,?)";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, entity.getId());
            ps.setString(2, entity.getFirstName());
            ps.setString(3, entity.getLastName());
            ps.setString(4, entity.getEmail());
            ps.setString(5, encryptPassword(entity.getPassword()));

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public User delete(Long aLong) {
        if (aLong == null)
            throw new IllegalArgumentException("id must be not null");

        String sql = "delete from users where id = ?";
        int rowCount = 0;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, aLong);
            rowCount = ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (rowCount > 0) {
            return findOne(aLong);
        }

        return null;
    }

    @Override
    public User update(User entity) {
        if (entity == null)
            throw new IllegalArgumentException("entity must be not null!");
        validator.validate(entity);

        String sql = "update users set first_name= ?,last_name= ?, email=?, password=? where id = ?";
        int rowCount = 0;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, entity.getFirstName());
            ps.setString(2, entity.getLastName());
            ps.setString(3, entity.getEmail());
            ps.setString(4, encryptPassword(entity.getPassword()));
            ps.setLong(5, entity.getId());
            rowCount = ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (rowCount > 0) {
            return null;
        }
        return entity;
    }

    private String encryptPassword(String password) {
        Encrypt smecherez = new Encrypt();
        password = smecherez.encryption(password);
        return password;
    }

    private String decryptPassword(String password) {
        Encrypt smecherez = new Encrypt();
        password = smecherez.decryption(password);
        return password;
    }

    public Set<User> findUserNoFriends(Long currentUserId) {
        Set<User> users = new HashSet<>();
        String sql = "SELECT * from users left join friendships on users.id = friendships.id1 or users.id = friendships.id2 " +
                "where friendships.id1 IS NULL and friendships.id2 IS NULL or " +
                "(friendships.id1 IS NOT NULL and friendships.id2 IS NOT  NULL AND (friendships.id1!=?  and friendships.id2!=?)) " +
                "and (users.id not in (select friendships.id1 from friendships where friendships.id2=?) and " +
                " users.id not in (select friendships.id2 from friendships where friendships.id1=?)) ";
        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setLong(1,currentUserId);
            statement.setLong(2,currentUserId);
            statement.setLong(3,currentUserId);
            statement.setLong(4,currentUserId);

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                long id = resultSet.getLong("id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String email = resultSet.getString("email");
                String password = resultSet.getString("password");


                User user = new User(firstName, lastName, email, decryptPassword(password));
                user.setId(id);
                users.add(user);
            }
            return users;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }


    @Override
    public Page<User> findAllPaginated(Pageable pageable, Long currentUserId) {
        Set<User> userNoFriends = findUserNoFriends(currentUserId);
        User currentUser = findOne(currentUserId);
        userNoFriends.remove(currentUser);

        Paginator<User> paginator = new Paginator<User>(pageable, userNoFriends);
        return paginator.paginate();
    }


}
