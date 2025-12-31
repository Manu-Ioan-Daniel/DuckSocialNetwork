package models;

public class ChatReplyMessageItem extends ChatMessageItem {
    private final String replyMessage;

    public ChatReplyMessageItem(Message message, String replyMessage, boolean isMine) {
        super(message, isMine);
        this.replyMessage = replyMessage;
    }

    public String replyMessage() { return replyMessage; }
}

