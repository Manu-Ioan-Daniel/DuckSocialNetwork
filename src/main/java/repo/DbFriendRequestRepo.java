package repo;
import domain.FriendRequest;
import exceptions.RepoException;
import utils.DbConnection;
import utils.Tuple;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class DbFriendRequestRepo implements Repository<Tuple<Long,Long>,FriendRequest> {

    private final Connection connection = DbConnection.getInstance().getConnection();

    @Override
    public Optional<FriendRequest> findOne(Tuple<Long, Long> id) {
        String sql = """
                SELECT *
                FROM friend_requests
                WHERE LEAST(from_id,to_id) = ? AND GREATEST(from_id,to_id) = ?
                """;

        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setLong(1,Long.min(id.getFirst(),id.getSecond()));
            ps.setLong(2,Long.max(id.getFirst(),id.getSecond()));
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next())
                    return Optional.of(friendRequestFromResultSet(rs));
            }
        }catch(SQLException e){
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public Iterable<FriendRequest> findAll() {
        String sql = """
                SELECT *
                FROM friend_requests
                """;
        Set<FriendRequest> friendRequests = new HashSet<>();
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    friendRequests.add(friendRequestFromResultSet(rs));
                }
            }
        }catch(SQLException e){
            throw new RuntimeException(e);
        }
        return friendRequests;
    }

    private Iterable<FriendRequest> findFriendRequestsByColumn(String columnName, Long id) {
        String sql = "SELECT * FROM friend_requests WHERE " + columnName + " = ?";
        Set<FriendRequest> friendRequests = new HashSet<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    friendRequests.add(friendRequestFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return friendRequests;
    }

    public Iterable<FriendRequest> findFriendRequestsOf(Long id) {
        return findFriendRequestsByColumn("to_id", id);
    }

    public Iterable<FriendRequest> findSentFriendRequests(Long id) {
        return findFriendRequestsByColumn("from_id", id);
    }



    @Override
    public void save(FriendRequest entity) {
        String sql = """
                INSERT INTO friend_requests(from_id,to_id,status)
                VALUES (?, ?, ?)
                """;
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setLong(1,entity.getId().getFirst());
            ps.setLong(2,entity.getId().getSecond());
            ps.setString(3,entity.getStatus());
            ps.executeUpdate();
        }catch(SQLException e){
            if(e.getSQLState().equals("23505")){
                throw new RepoException("You have already friend requested this user!");
            }
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(Tuple<Long, Long> id) {
        String sql = """
                DELETE
                FROM friend_requests
                WHERE LEAST(from_id,to_id) = ? AND GREATEST(from_id,to_id) = ?
                """;
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setLong(1,Long.min(id.getFirst(),id.getSecond()));
            ps.setLong(2,Long.max(id.getFirst(),id.getSecond()));
            ps.executeUpdate();
        }catch(SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(FriendRequest entity) {
        String sql = """
                UPDATE friend_requests
                SET status = ?
                WHERE LEAST(from_id,to_id) = ? AND GREATEST(from_id,to_id) = ?
                """;
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setString(1,entity.getStatus());
            ps.setLong(2,Long.min(entity.getId().getFirst(),entity.getId().getSecond()));
            ps.setLong(3,Long.max(entity.getId().getFirst(),entity.getId().getSecond()));
            ps.executeUpdate();
        }catch(SQLException e){
            throw new RuntimeException(e);
        }
    }

    private FriendRequest friendRequestFromResultSet(ResultSet rs) throws SQLException {
        Long fromId = rs.getLong(1);
        Long toId = rs.getLong(2);
        String status = rs.getString(3);
        FriendRequest fr = new FriendRequest(status);
        fr.setId(new Tuple<>(fromId,toId));
        return fr;
    }
}
