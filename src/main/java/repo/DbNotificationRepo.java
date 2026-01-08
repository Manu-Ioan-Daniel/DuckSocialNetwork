package repo;

import exceptions.RepoException;
import models.Notification;
import utils.DbConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class DbNotificationRepo implements Repository<Long, Notification> {

    private Connection connection = DbConnection.getInstance().getConnection();
    public DbNotificationRepo() {

    }
    @Override
    public Optional<Notification> findOne(Long id) {
            String sql = """
                    SELECT *
                    FROM notifications
                    WHERE id = ?
                    """;
            try(PreparedStatement ps = connection.prepareStatement(sql)){
                ps.setLong(1, id);
                try(ResultSet rs = ps.executeQuery()){
                    if(rs.next()){
                        return Optional.of(notificationFromResultSet(rs));
                    }
                }
            }catch(SQLException e){
                throw new RuntimeException(e);
            }
            return Optional.empty();
    }

    private Notification notificationFromResultSet(ResultSet rs) throws SQLException {
        Long id = rs.getLong(1);
        Long userId = rs.getLong(2);
        String message = rs.getString(3);
        Notification notification = new Notification(message, userId);
        notification.setId(id);
        return notification;
    }

    @Override
    public Iterable<Notification> findAll() {
        String sql = """
                SELECT *
                from notifications
                """;
        Set<Notification> notifications = new HashSet<>();
        try(PreparedStatement ps = connection.prepareStatement(sql);ResultSet rs = ps.executeQuery()){
            while(rs.next()){
                notifications.add(notificationFromResultSet(rs));
            }
        }catch(SQLException e){
            throw new RuntimeException(e);
        }
        return notifications;
    }

    public Iterable<Notification> findUserNotifications(Long userId){
        String sql = """
                SELECT *
                FROM notifications
                WHERE user_id = ?
        """;
        Set<Notification> notifications = new HashSet<>();
        try(PreparedStatement ps =connection.prepareStatement(sql)){
            ps.setLong(1, userId);
            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    notifications.add(notificationFromResultSet(rs));
                }
            }
        }catch(SQLException e){
            throw new RuntimeException(e);
        }
        return notifications;
    }

    @Override
    public void save(Notification entity) {
        String sql = """
                INSERT INTO notifications(user_id,message)
                VALUES (?, ?)
                """;
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setLong(1,entity.getUserId());
            ps.setString(2, entity.getMessage());
            ps.executeUpdate();
        }catch(SQLException e){
            //if condition should never happen
            if(e.getSQLState().equals("23505"))
                throw new RepoException("Notification already exists");
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(Long id) {
        String sql = """
                DELETE FROM notifications
                WHERE id = ?
                """;
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setLong(1,id);
            ps.executeUpdate();
        }catch(SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Notification entity) {

    }
}
