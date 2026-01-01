package service;

import exceptions.ValidationException;
import models.Friendship;
import enums.ChangeEvent;
import repo.DbFriendshipRepo;
import utils.Tuple;
import utils.observer.Observable;
import validation.Validator;
import java.util.ArrayList;
import java.util.List;

public class FriendshipService extends Observable {
    private final DbFriendshipRepo friendshipRepo;
    private final Validator<Long> idValidator;
    
    public FriendshipService(DbFriendshipRepo friendshipRepo, Validator<Long> idValidator) {
        this.friendshipRepo = friendshipRepo;
        this.idValidator = idValidator;
    }
    
    public void save(Friendship friendship){
        if(friendship == null){
            throw new ValidationException("Invalid friendship!");
        }
        friendshipRepo.save(friendship);
        notifyObservers(ChangeEvent.FRIENDSHIP_DATA);
    }

    public void delete(Long id1,Long id2){
        idValidator.validate(id1);
        idValidator.validate(id2);
        friendshipRepo.delete(new Tuple<>(Long.min(id1,id2),Long.max(id1,id2)));
        notifyObservers(ChangeEvent.FRIENDSHIP_DATA);
    }

    public List<Long> findFriendsOf(Long id){
        idValidator.validate(id);
        List<Long> friendIds = new ArrayList<>();
        for(Long friendId:friendshipRepo.findFriendsOf(id)){
            friendIds.add(friendId);
        }
        return friendIds;
    }

}
