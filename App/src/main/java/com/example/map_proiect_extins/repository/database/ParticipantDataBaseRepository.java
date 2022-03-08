package com.example.map_proiect_extins.repository.database;

import com.example.map_proiect_extins.domain.*;
import com.example.map_proiect_extins.repository.Repository;
import com.example.map_proiect_extins.validators.Validator;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class ParticipantDataBaseRepository implements Repository<Pair<Long>, EventParticipant> {
    private String url;
    private String username;
    private String password;
    Validator<EventParticipant> participantValidator;

    public ParticipantDataBaseRepository(String url, String username, String password, Validator<EventParticipant> participantValidator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.participantValidator = participantValidator;
    }

    @Override
    public EventParticipant findOne(Pair<Long> longPair) {
        if (longPair.getFirst() == null || longPair.getSecond() == null)
            throw new IllegalArgumentException("IDs must be not null");

        String sql = "select * from participants where id_participant = ? and id_event = ?";
        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setLong(1, longPair.getFirst());
            ps.setLong(2, longPair.getSecond());
            ResultSet resultSet = ps.executeQuery();

            while (resultSet.next()) {
                Long id_user = resultSet.getLong("id_participant");
                Long id_event = resultSet.getLong("id_event");
                Boolean notifications = resultSet.getBoolean("notifications");

                EventParticipant participant = new EventParticipant(id_user,id_event);
                participant.setNotifications(notifications);
                return participant;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Iterable<EventParticipant> findAll() {
        Set<EventParticipant> participants = new HashSet<>();
        String sql = "select * from participants ";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id_user = resultSet.getLong("id_participant");
                Long id_event = resultSet.getLong("id_event");
                Boolean notifications = resultSet.getBoolean("notifications");

                EventParticipant participant = new EventParticipant(id_user,id_event);
                participant.setNotifications(notifications);

                participants.add(participant);
            }
            return participants;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return participants;
    }

    @Override
    public EventParticipant save(EventParticipant entity) {
        if (entity == null)
            throw new IllegalArgumentException("entity must be not null");
        participantValidator.validate(entity);

        String sql = "insert into participants (id_participant,id_event,notifications) values (?,?,?)";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, entity.getId().getFirst());
            ps.setLong(2, entity.getId().getSecond());
            ps.setBoolean(3, entity.getNotifications());


            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public EventParticipant delete(Pair<Long> longPair) {
        if (longPair.getFirst() == null || longPair.getSecond() == null)
            throw new IllegalArgumentException("IDs must be not null");

        String sql = "delete from participants where id_participant = ? and id_event = ?";
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
            return findOne(longPair);
        }

        return null;
    }

    @Override
    public EventParticipant update(EventParticipant entity) {
        if (entity.getId().getFirst() == null || entity.getId().getSecond() == null)
            throw new IllegalArgumentException("IDs must be not null");
        participantValidator.validate(entity);

        String sql = "update participants set notifications = ? where id_participant = ? and id_event = ?";
        int rowCount = 0;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setBoolean(1, entity.getNotifications());
            ps.setLong(2, entity.getId().getFirst());
            ps.setLong(3, entity.getId().getSecond());

            rowCount = ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (rowCount > 0) {
            return null;
        }
        return entity;
    }
}
