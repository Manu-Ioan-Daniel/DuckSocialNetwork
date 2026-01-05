package repo;

import exceptions.RepoException;
import models.RaceEvent;
import utils.DbConnection;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class DbEventRepo implements Repository<Long, RaceEvent> {
    private final Connection connection = DbConnection.getInstance().getConnection();


    @Override
    public Optional<RaceEvent> findOne(Long id) {
        String sql = """
        SELECT * FROM events
        WHERE id = ?
        """;

        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setLong(1,id);
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    return Optional.of(eventFromResultSet(rs));
                }
            }
        }catch(SQLException e){
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    private RaceEvent eventFromResultSet(ResultSet rs) throws SQLException {
        Long id = rs.getLong(1);
        String name = rs.getString(2);
        RaceEvent event = new RaceEvent(name);
        event.setId(id);
        return event;
    }

    @Override
    public Iterable<RaceEvent> findAll() {
        String sql = """
                SELECT * FROM events
                """;
        Set<RaceEvent> raceEvents = new HashSet<>();
        try(PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()){
            while(rs.next()){
                raceEvents.add(eventFromResultSet(rs));
            }
        }catch(SQLException e){
            throw new RuntimeException(e);
        }
        return raceEvents;
    }

    @Override
    public void save(RaceEvent entity) {
        String sql = """
                INSERT INTO events(name)
                VALUES (?)
                """;
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setString(1, entity.getName());
            ps.executeUpdate();
        }catch(SQLException e){
            throw  new RuntimeException(e);
        }
    }

    @Override
    public void delete(Long id) {
        String sql = """
                DELETE FROM events
                WHERE id = ?
                """;
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setLong(1, id);
            ps.executeUpdate();
        }catch(SQLException e){
            throw new RuntimeException(e);
        }
    }


    @Override
    public void update(RaceEvent entity) {

    }

    public void addSubscriber(Long eventId,Long subscriberId){
        if(getSubscribers(eventId).contains(subscriberId)){
            throw new RepoException("User is already subscribed to this event!");
        }
        String sql = """
                INSERT INTO event_members(user_id,event_id,date)
                VALUES (?, ?, ?)
                """;
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setLong(1,subscriberId);
            ps.setLong(2,eventId);
            ps.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            ps.executeUpdate();
        }catch(SQLException e){
            throw new RuntimeException(e);
        }
    }

    public List<Long> getSubscribers(Long eventId){
        String sql = """
                SELECT user_id from event_members
                WHERE event_id = ?
                """;
        List<Long> subscribers = new ArrayList<>();
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setLong(1,eventId);
            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    subscribers.add(rs.getLong(1));
                }
            }
        }catch(SQLException e){
            throw new RuntimeException(e);
        }
        return subscribers;
    }
}
