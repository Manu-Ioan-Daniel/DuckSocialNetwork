package service;

import models.Message;

import models.ReplyMessage;
import enums.ChangeEvent;
import repo.DbMessageRepo;
import utils.Tuple;
import utils.observer.Observable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MessageService extends Observable {
    private final DbMessageRepo messageRepo;
    public MessageService(DbMessageRepo messageRepo) {
        this.messageRepo = messageRepo;
    }

    public List<Message> findConversation(Long id1, Long id2){
        List<Message> msgs = new ArrayList<>();
        for(Message m : messageRepo.findConversation(new Tuple<>(id1,id2))){
            msgs.add(m);
        }
        return msgs;
    }

    public void save(Long fromId, Long toId, String text, Message repliedToMessage){
        if(repliedToMessage == null){
            messageRepo.save(new Message(text, LocalDateTime.now(), fromId, toId));
            notifyObservers(ChangeEvent.MESSAGE_EVENT);
            return;
        }
        messageRepo.save(new ReplyMessage(text,LocalDateTime.now(),fromId,toId,repliedToMessage.getId()));
        notifyObservers(ChangeEvent.MESSAGE_EVENT);
    }

    public Message findOne(Long messageId) {
        return messageRepo.findOne(messageId).orElse(null);
    }
}
