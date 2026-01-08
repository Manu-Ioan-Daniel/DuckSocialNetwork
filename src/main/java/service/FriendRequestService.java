package service;

import models.FriendRequest;
import enums.ChangeEvent;
import exceptions.ValidationException;
import repo.DbFriendRequestRepo;
import utils.Tuple;
import utils.observer.Observable;
import validation.Validator;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class FriendRequestService extends Observable {

    private final DbFriendRequestRepo friendRequestRepo;
    private final Validator<Long> idValidator;
    

    public FriendRequestService(DbFriendRequestRepo friendRequestRepo,Validator<Long> idValidator) {

        this.friendRequestRepo = friendRequestRepo;
        this.idValidator = idValidator;
    }
    

    public Optional<FriendRequest> findOne(Long id1, Long id2){
        idValidator.validate(id1);
        idValidator.validate(id2);
        return friendRequestRepo.findOne(new Tuple<>(id1,id2));
    }


    public List<FriendRequest> findFriendRequestsOf(Long id){
        idValidator.validate(id);
        List<FriendRequest> list = new ArrayList<>();
        friendRequestRepo.findFriendRequestsOf(id).forEach(list::add);
        return list;
    }

    public List<FriendRequest> findSentFriendRequests(Long id){
        idValidator.validate(id);
        List<FriendRequest> list = new ArrayList<>();
        friendRequestRepo.findSentFriendRequests(id).forEach(list::add);
        return list;
    }

    public void save(FriendRequest friendRequest){
        if(friendRequest == null){
            throw new ValidationException("Invalid request");
        }
        friendRequestRepo.save(friendRequest);
        notifyObservers(ChangeEvent.FRIEND_REQUEST_DATA);
    }
    public void delete(Long id1, Long id2){
        idValidator.validate(id1);
        idValidator.validate(id2);
        friendRequestRepo.delete(new Tuple<>(id1,id2));
        notifyObservers(ChangeEvent.FRIEND_REQUEST_DATA);
    }
    public void update(FriendRequest friendRequest){
        if(friendRequest == null){
            throw new ValidationException("Invalid request");
        }
        friendRequestRepo.update(friendRequest);
        notifyObservers(ChangeEvent.FRIEND_REQUEST_DATA);
    }

}
