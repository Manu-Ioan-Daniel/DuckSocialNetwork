package models;

import domain.Message;
import repo.DbMessageRepo;
import utils.Tuple;
import utils.observer.NotificationHandler;
import utils.observer.Observable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MessageModel extends Observable {
    private DbMessageRepo messageRepo;
    public MessageModel(DbMessageRepo messageRepo) {
        this.messageRepo = messageRepo;
        this.addObserver(NotificationHandler.getInstance());
    }

    public List<Message> findConversation(Long id1, Long id2){
        List<Message> msgs = new ArrayList<>();
        for(Message m : messageRepo.findConversation(new Tuple<>(id1,id2))){
            msgs.add(m);
        }
        return msgs;
    }
    public void save(Long fromId,Long toId,String text){
        messageRepo.save(new Message(text, LocalDateTime.now(), fromId, toId));
    }

}
