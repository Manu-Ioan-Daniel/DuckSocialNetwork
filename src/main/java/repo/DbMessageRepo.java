package repo;

import models.Message;
import models.ReplyMessage;
import utils.DbConnection;
import utils.Tuple;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class DbMessageRepo implements Repository<Long, Message> {
    private final Connection connection = DbConnection.getInstance().getConnection();
    public DbMessageRepo() {}


    @Override
    public Optional<Message> findOne(Long id) {
        String sql = """
                SELECT M.id,M.fromId,M.toId,M.message,M.date,R.message_id
                FROM messages M
                LEFT JOIN reply_messages R ON R.id = M.id
                WHERE M.id = ?
                """;
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                return Optional.of(messageFromResultSet(rs));
            }
        }catch(SQLException e){
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }


    private Message messageFromResultSet(ResultSet rs) throws SQLException {
        Long id =  rs.getLong(1);
        Long fromId = rs.getLong(2);
        Long toId = rs.getLong(3);
        String message = rs.getString(4);
        LocalDateTime date = rs.getTimestamp(5).toLocalDateTime();
        Long messageId = rs.getLong(6);

        if(rs.wasNull()) {
            Message m = new Message(message,date,fromId,toId);
            m.setId(id);
            return m;
        }
        ReplyMessage rm = new ReplyMessage(message,date,fromId,toId,messageId);
        rm.setId(id);
        return rm;
    }

    public Iterable<Message> findConversation(Tuple<Long,Long> ids){
        List<Message> conversation = new ArrayList<>();
        String sql = """
                SELECT M.id,M.fromId,M.toId,M.message,M.date,R.message_id
                FROM messages M
                LEFT JOIN reply_messages R ON R.id = M.id
                WHERE (M.fromId = ? AND M.toId = ?) OR (M.fromId = ? AND M.toId = ?)
                ORDER BY M.date;
                """;
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setLong(1, ids.getFirst());
            ps.setLong(2, ids.getSecond());
            ps.setLong(3,ids.getSecond());
            ps.setLong(4,ids.getFirst());
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                conversation.add(messageFromResultSet(rs));
            }
        }catch(SQLException e){
            throw new RuntimeException(e);
        }
        return conversation;
    }

    @Override
    public Iterable<Message> findAll() {
        return null;
    }

    @Override
    public void save(Message entity) {
        String sql = """
                INSERT INTO messages(fromId,toId,message,date) VALUES (?,?,?,?)
                """;
        try(PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, entity.getFromId());
            ps.setLong(2, entity.getToId());
            ps.setString(3, entity.getMessage());
            ps.setTimestamp(4, Timestamp.valueOf(entity.getDateTime()));
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            long id = 0;
            if(rs.next()){
                id = rs.getLong(1);
            }
            if(entity instanceof ReplyMessage rm) {
                String sql2 = """
                        INSERT INTO reply_messages(id,message_id) VALUES (?,?)
                        """;
                try(PreparedStatement ps2 = connection.prepareStatement(sql2)){
                    if(id!=0)
                        ps2.setLong(1, id);
                    else
                        throw new SQLException("Generated key failure");
                    ps2.setLong(2,rm.getReplyMessageId());
                    ps2.executeUpdate();

                }
            }
        }catch(SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(Long aLong) {

    }

    @Override
    public void update(Message entity) {

    }
}
