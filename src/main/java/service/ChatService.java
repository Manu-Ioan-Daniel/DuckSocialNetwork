package service;

import domain.*;
import models.MessageModel;
import models.UserModel;
import utils.Models;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ChatService{
    private final MessageModel messageModel;
    private final UserModel userModel;
    public ChatService(UserModel userModel,MessageModel messageModel) {
        this.userModel = userModel;
        this.messageModel = messageModel;
    }

    public List<String> getUsernames(){
        return userModel.findAll().stream()
                .map(User::getUsername)
                .toList();
    }

    public FriendRequest getLastFriendRequest(){
        return Models.getFriendRequestModel().getLastFriendRequest();
    }

    public User getUser(String username){
        Optional<User> user =  userModel.findOne(username);
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
                items.add(new ChatReplyMessageItem(message,messageModel.findOne(rm.getReplyMessageId()).getMessage(),rm.getFromId().equals(currentUserId)));
            else
                items.add(new ChatMessageItem(message, message.getFromId().equals(currentUserId)));
            lastSenderId = message.getFromId();
        }

        return items;
    }


    public List<Message> loadConversation(String username1, String username2) {
        Long id1 = userModel.findOne(username1).map(User::getId).orElse(null);
        Long id2 = userModel.findOne(username2).map(User::getId).orElse(null);

        if(id1==null || id2==null){
            throw  new IllegalArgumentException("Username not found");
        }

        return messageModel.findConversation(id1,id2);
    }

    public void saveMessage(String fromUsername, String toUsername, String text,Message repliedToMessage) {

        messageModel.save(getUser(fromUsername).getId(), getUser(toUsername).getId(), text,repliedToMessage);
    }
}
