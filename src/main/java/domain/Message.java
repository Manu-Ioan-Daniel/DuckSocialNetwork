package domain;

import java.time.LocalDateTime;

public class Message extends Entity<Long>{
    private final Long fromId;
    private final Long toId;
    private final String message;
    private final LocalDateTime dateTime;

    public Message(String message, LocalDateTime dateTime, Long fromId, Long toId) {
        this.message = message;
        this.dateTime = dateTime;
        this.fromId = fromId;
        this.toId = toId;
    }

    public Long getFromId() {
        return fromId;
    }

    public Long getToId() {
        return toId;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }
}
