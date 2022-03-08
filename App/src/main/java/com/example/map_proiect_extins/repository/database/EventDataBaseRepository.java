package com.example.map_proiect_extins.repository.database;

import com.example.map_proiect_extins.domain.Event;
import com.example.map_proiect_extins.domain.Message;
import com.example.map_proiect_extins.domain.User;
import com.example.map_proiect_extins.repository.Repository;
import com.example.map_proiect_extins.validators.Validator;
import com.example.map_proiect_extins.utils.Encrypt;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class EventDataBaseRepository implements Repository<Long, Event> {

    private String url;
    private String username;
    private String password;
    Validator<Event> eventsValidator;

    public EventDataBaseRepository(String url, String username, String password, Validator<Event> eventsValidator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.eventsValidator = eventsValidator;
    }

    @Override
    public Event findOne(Long aLong) {
        if(aLong == null)
            throw new IllegalArgumentException("ID must be not null!\n");

        String sql = "SELECT events.id AS event_id, name, description, start_date,finish_date, organiser, first_name,last_name,email,password from events left join users on users.id = events.organiser where events.id = ?";

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setLong(1, aLong);
            ResultSet resultSet = ps.executeQuery();

            while (resultSet.next()) {
                Long id = resultSet.getLong("event_id");
                String name = resultSet.getString("name");
                String description = resultSet.getString("description");
                Timestamp startDate = resultSet.getTimestamp("start_date");
                Timestamp finishDate = resultSet.getTimestamp("finish_date");

                Long idUser = resultSet.getLong("organiser");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String email = resultSet.getString("email");
                String password = resultSet.getString("password");

                User user = new User(firstName, lastName, email, decryptPassword(password));
                user.setId(idUser);

                Event event = new Event(name, description, startDate.toLocalDateTime(), finishDate.toLocalDateTime(), user);
                event.setId(id);
                return event;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Iterable<Event> findAll() {
        Set<Event> events = new HashSet<>();
        String sql = "SELECT events.id AS event_id, name, description, start_date,finish_date, organiser, first_name,last_name,email,password from events left join users on users.id = events.organiser";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("event_id");
                String name = resultSet.getString("name");
                String description = resultSet.getString("description");
                Timestamp startDate = resultSet.getTimestamp("start_date");
                Timestamp finishDate = resultSet.getTimestamp("finish_date");

                Long idUser = resultSet.getLong("organiser");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String email = resultSet.getString("email");
                String password = resultSet.getString("password");

                User user = new User(firstName, lastName, email, decryptPassword(password));
                user.setId(idUser);

                Event event = new Event(name, description, startDate.toLocalDateTime(), finishDate.toLocalDateTime(), user);
                event.setId(id);

                events.add(event);
            }
            return events;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return events;
    }

    @Override
    public Event save(Event entity) {
        if (entity == null)
            throw new IllegalArgumentException("entity must be not null");
        eventsValidator.validate(entity);

        String sql = "insert into events (id,name, description, start_date, finish_date, organiser ) values (?,?,?,?,?,?)";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            Timestamp timestampStart = Timestamp.valueOf(entity.getStartDate());
            Timestamp timestampFinish = Timestamp.valueOf(entity.getFinishDate());

            ps.setLong(1, entity.getId());
            ps.setString(2, entity.getName());
            ps.setString(3, entity.getDescription());
            ps.setTimestamp(4, timestampStart);
            ps.setTimestamp(5,timestampFinish);
            ps.setLong(6, entity.getOrganiser().getId());

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Event delete(Long aLong) {
        if (aLong == null)
            throw new IllegalArgumentException("ID must be not null");

        String sql = "delete from events where id = ?";
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
    public Event update(Event entity) {
        //UPDATE: Nume, Descr, DateS, DateF, Org
        //no update needed for now
        return null;
    }

    private String decryptPassword(String password) {
        Encrypt smecherez = new Encrypt();
        password = smecherez.decryption(password);
        return password;
    }


}
