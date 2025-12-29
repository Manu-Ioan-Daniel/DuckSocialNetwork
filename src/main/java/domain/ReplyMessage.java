package domain;

import java.time.LocalDateTime;

public class ReplyMessage extends Message{
    private final Long replyMessageId;

    public ReplyMessage(String message, LocalDateTime dateTime, Long fromId, Long toId, Long replyMessageId) {
        super(message, dateTime, fromId, toId);
        this.replyMessageId = replyMessageId;
    }
    public ReplyMessage(Message message){
        super(message.getMessage(), LocalDateTime.now(), message.getToId(), message.getFromId());
        this.replyMessageId = message.getId();
    }


    public Long getReplyMessageId() {
        return replyMessageId;
    }
}
