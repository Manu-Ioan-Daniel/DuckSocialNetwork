package service;

import models.*;
import utils.Services;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ChatService{
    private final MessageService messageService;
    private final UsersService usersService;
    public ChatService(UsersService usersService, MessageService messageService) {
        this.usersService = usersService;
        this.messageService = messageService;
    }

    public List<String> getUsernames(){
        return usersService.findAll().stream()
                .map(User::getUsername)
                .toList();
    }

    public FriendRequest getLastFriendRequest(){
        return Services.getFriendRequestService().getLastFriendRequest();
    }

    public User getUser(String username){
        Optional<User> user =  usersService.findOne(username);
        if(user.isPresent()){
            return user.get();
        }else{
            throw new IllegalArgumentException("Username not found");
        }
    }

    public List<ChatItem> buildChatItems(String currentUsername, String otherUsername) {
        List<Message> messages = loadConversation(currentUsername, otherUsername);

        Long currentUserId = getUser(currentUsername).getId();
        Long lastSenderId = null;

        List<ChatItem> items = new ArrayList<>();

        for (Message message : messages) {
            if (!message.getFromId().equals(lastSenderId)) {
                boolean isMine = message.getFromId().equals(currentUserId);
                String headerText = isMine ? "You" : otherUsername;
                items.add(new ChatHeaderItem(headerText, isMine));
            }
            if(message instanceof ReplyMessage rm)
                items.add(new ChatReplyMessageItem(message, messageService.findOne(rm.getReplyMessageId()).orElseThrow().getMessage(),rm.getFromId().equals(currentUserId)));
            else
                items.add(new ChatMessageItem(message, message.getFromId().equals(currentUserId)));
            lastSenderId = message.getFromId();
        }

        return items;
    }


    public List<Message> loadConversation(String username1, String username2) {
        Long id1 = usersService.findOne(username1).map(User::getId).orElse(null);
        Long id2 = usersService.findOne(username2).map(User::getId).orElse(null);

        if(id1==null || id2==null){
            throw  new IllegalArgumentException("Username not found");
        }

        return messageService.findConversation(id1,id2);
    }

    public void saveMessage(String fromUsername, String toUsername, String text,Message repliedToMessage) {
        messageService.save(getUser(fromUsername).getId(), getUser(toUsername).getId(), text,repliedToMessage);
    }
}
