package domain;

public record ChatMessageItem(Message message,boolean isMine) implements ChatItem {

}
