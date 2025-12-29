package domain;

public class ChatMessageItem implements ChatItem {
    private final Message message;
    private final boolean isMine;

    public ChatMessageItem(Message message, boolean isMine) {
        this.message = message;
        this.isMine = isMine;
    }

    public Message message() { return message; }
    public boolean isMine() { return isMine; }
}

