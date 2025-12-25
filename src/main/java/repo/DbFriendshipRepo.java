package repo;

import domain.Friendship;
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

public class DbFriendshipRepo implements Repository<Tuple<Long,Long>, Friendship> {
    private final Connection connection;
    public DbFriendshipRepo(){
        connection = DbConnection.getInstance().getConnection();
    }
    @Override
    public Optional<Friendship> findOne(Tuple<Long, Long> id) {
        String sql = """
                SELECT user_id_1,user_id_2
                FROM friendships
                WHERE user_id_1 = ? AND user_id_2 = ?;
                """;
        long id1 = Long.min(id.getFirst(), id.getSecond());
        long id2 = Long.max(id.getFirst(), id.getSecond());
        try(PreparedStatement ps = connection.prepareStatement(sql) ){

            ps.setLong(1, id1);
            ps.setLong(2, id2);

            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){

                    Friendship friendship = new Friendship();
                    friendship.setId(new Tuple<>(id1,id2));
                    return Optional.of(friendship);

                }
            }

        }catch(SQLException e){
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public Iterable<Friendship> findAll() {
        String sql = """
                SELECT user_id_1,user_id_2
                FROM friendships
                """;
        Set<Friendship> friendships = new HashSet<>();
        try(PreparedStatement ps = connection.prepareStatement(sql);ResultSet rs = ps.executeQuery() ){

            while(rs.next()){

                Friendship friendship = new Friendship();
                friendship.setId(new Tuple<>(rs.getLong(1),rs.getLong(2)));
                friendships.add(friendship);

            }


        }catch(SQLException e){
            throw new RuntimeException(e);
        }
        return friendships;
    }

    public Iterable<Long> findFriendsOf(Long id){
        String sql = """
                SELECT user_id_1,user_id_2
                FROM friendships
                WHERE user_id_1 = ? OR user_id_2 = ?
        """;
        Set<Long> friendIds = new HashSet<>();
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setLong(1, id);
            ps.setLong(2, id);
            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    long id1 = rs.getLong(1);
                    long id2 = rs.getLong(2);
                    friendIds.add(id1 == id ? id2 : id1);
                }
            }
        }catch(SQLException e){
            throw new RuntimeException(e);
        }
        return friendIds;
    }

    @Override
    public void save(Friendship entity) {
        String sql = """
                INSERT INTO friendships(user_id_1,user_id_2)
                VALUES (?,?);
                """;
        try(PreparedStatement ps = connection.prepareStatement(sql)){

            ps.setLong(1, entity.getId().getFirst());
            ps.setLong(2, entity.getId().getSecond());
            ps.executeUpdate();

        }catch(SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(Tuple<Long, Long> id) {
        Optional<Friendship> friendship = findOne(id);
        if(friendship.isPresent()){
            String sql = """
                    DELETE FROM friendships
                    WHERE user_id_1 = ? AND user_id_2 = ?;
            """;
            try(PreparedStatement ps = connection.prepareStatement(sql)){
                ps.setLong(1, id.getFirst());
                ps.setLong(2, id.getSecond());
                ps.executeUpdate();
            }catch(SQLException e){
                throw new RuntimeException(e);
            }
        }else{
            throw new RepoException("Friendship does not exist!");
        }
    }

    @Override
    public void update(Friendship entity) {

    }

}
