package com.example.map_proiect_extins.repository.database;

import com.example.map_proiect_extins.domain.Message;
import com.example.map_proiect_extins.domain.User;
import com.example.map_proiect_extins.repository.Repository;
import com.example.map_proiect_extins.validators.Validator;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MessageDataBaseRepository implements Repository<Long, Message> {
    private String url;
    private String username;
    private String password;
    Validator<Message> messageValidator;

    public MessageDataBaseRepository(String url, String username, String password, Validator<Message> messageValidator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.messageValidator = messageValidator;
    }

    @Override
    public Message findOne(Long aLong) {
        List<User> to = new ArrayList<>();
        Long replyId = -1L;
        Message message = null;
        String sql = "select m.message_id,m.message,m.data,m.reply_message_id," +
                "u.id as from_user_id,u.first_name as from_first_name,u.last_name as from_last_name, u.email as from_email, u.password as from_password," +
                "u2.id as to_user_id,u2.first_name as to_first_name,u2.last_name as to_last_name, u2.email as to_email, u2.password as to_password " +
                "from conversations as c " +
                "inner join messages as m " +
                "on c.message_id=m.message_id " +
                "inner join users as u on u.id=c.from_user_id " +
                "inner join users as u2 on u2.id=c.to_user_id " +
                "where m.message_id= ? ";

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setLong(1, aLong);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                Long messageId = resultSet.getLong("message_id");
                String messageText = resultSet.getString("message");
                Timestamp date = resultSet.getTimestamp("data");
                replyId = resultSet.getLong("reply_message_id");
                Long fromUserId = resultSet.getLong("from_user_id");
                String fromFirstName = resultSet.getString("from_first_name");
                String fromLastName = resultSet.getString("from_last_name");
                String fromEmail = resultSet.getString("from_email");
                String fromPassword = resultSet.getString("from_password");
                Long toUserId = resultSet.getLong("to_user_id");
                String toFirstName = resultSet.getString("to_first_name");
                String toLastName = resultSet.getString("to_last_name");
                String toEmail = resultSet.getString("to_email");
                String toPassword = resultSet.getString("to_password");


                User userFrom = new User(fromFirstName, fromLastName, fromEmail, fromPassword);
                userFrom.setId(fromUserId);
                message = new Message(userFrom, messageText);
                message.setId(messageId);
                message.setData(date.toLocalDateTime());
                //message.setReplyMessage(findOne());

                User userTo = new User(toFirstName, toLastName, toEmail, toPassword);
                userTo.setId(toUserId);
                to.add(userTo);

                while (resultSet.next()) {
                    Long toUserIdNext = resultSet.getLong("to_user_id");
                    String toFirstNameNext = resultSet.getString("to_first_name");
                    String toLastNameNext = resultSet.getString("to_last_name");
                    String toEmailNext = resultSet.getString("to_email");
                    String toPasswordNext = resultSet.getString("to_password");
                    User userToNext = new User(toFirstNameNext, toLastNameNext, toEmailNext, toPasswordNext);
                    userToNext.setId(toUserIdNext);
                    to.add(userToNext);
                }
                message.setTo(to);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (replyId != -1) {
            message.setReplyMessage(findOne(replyId));
        }
        return message;
    }

    @Override
    public Iterable<Message> findAll() {
        return null;

    }

    @Override
    public Message save(Message entity) {
        if (entity == null)
            throw new IllegalArgumentException("entity must be not null");
        messageValidator.validate(entity);

        String sql = "insert into messages (message_id,message,data,reply_message_id) values (?,?,?,?)";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, entity.getId());
            ps.setString(2, entity.getMessage());
            ps.setTimestamp(3, Timestamp.valueOf(entity.getData()));
            if (entity.getReplyMessage() != null)
                ps.setLong(4, entity.getReplyMessage().getId());
            else {
                ps.setLong(4, -1L);
            }

            ps.executeUpdate();

            for (User user : entity.getTo()) {
                String sql1 = "insert into conversations(from_user_id, message_id, to_user_id) values (?,?,?) ";
                PreparedStatement ps1 = connection.prepareStatement(sql1);

                ps1.setLong(1, entity.getFrom().getId());
                ps1.setLong(2, entity.getId());
                ps1.setLong(3, user.getId());
                ps1.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Message delete(Long aLong) {
        return null;
    }

    @Override
    public Message update(Message entity) {
        return null;

    }

    public Iterable<Message> findConversation(Long userId1, Long userId2) {
        Set<Message> conversations = new HashSet<>();

        String sql = "select c.from_user_id, c.message_id, c.to_user_id,m.message_id, m.message,m.data,m.reply_message_id " +
                "from conversations as c inner join messages as m on " +
                "c.message_id=m.message_id " +
                "where (c.from_user_id=? and c.to_user_id=?) or (c.from_user_id=? and c.to_user_id=?) order by m.data";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, userId1);
            ps.setLong(2, userId2);
            ps.setLong(3, userId2);
            ps.setLong(4, userId1);
            ResultSet resultSet = ps.executeQuery();

            while (resultSet.next()) {
                Long messageId = resultSet.getLong("message_id");
                Message message = findOne(messageId);
                conversations.add(message);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return conversations;
    }

    public Long findLastId() {
        String sql = "select * from messages order by message_id desc";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet resultSet = ps.executeQuery();

            if (resultSet.next()) {
                return resultSet.getLong("message_id");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0L;
    }
}
