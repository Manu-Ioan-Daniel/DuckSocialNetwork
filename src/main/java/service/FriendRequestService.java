package service;

import models.FriendRequest;
import enums.ChangeEvent;
import exceptions.ValidationException;
import repo.DbFriendRequestRepo;
import utils.Tuple;
import utils.observer.Observable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class FriendRequestService extends Observable {

    private final DbFriendRequestRepo friendRequestRepo;
    private FriendRequest lastFriendRequestSaved;

    public FriendRequestService(DbFriendRequestRepo friendRequestRepo) {

        this.friendRequestRepo = friendRequestRepo;
    }

    private void validateId(Long id){
        if(id == null || id<0){
            throw new ValidationException("Invalid id");
        }
    }

    public Optional<FriendRequest> findOne(Long id1, Long id2){
        validateId(id1);
        validateId(id2);
        return friendRequestRepo.findOne(new Tuple<>(id1,id2));
    }


    public List<FriendRequest> findFriendRequestsOf(Long id){
        validateId(id);
        List<FriendRequest> list = new ArrayList<>();
        friendRequestRepo.findFriendRequestsOf(id).forEach(list::add);
        return list;
    }

    public List<FriendRequest> findSentFriendRequests(Long id){
        validateId(id);
        List<FriendRequest> list = new ArrayList<>();
        friendRequestRepo.findSentFriendRequests(id).forEach(list::add);
        return list;
    }

    public void save(FriendRequest friendRequest){
        if(friendRequest == null){
            throw new ValidationException("Invalid request");
        }
        friendRequestRepo.save(friendRequest);
        lastFriendRequestSaved = friendRequest;
        notifyObservers(ChangeEvent.SENT_FRIEND_REQUEST);
    }
    public void delete(Long id1, Long id2){
        validateId(id1);
        validateId(id2);
        friendRequestRepo.delete(new Tuple<>(id1,id2));
        notifyObservers(ChangeEvent.FRIEND_REQUEST_DATA);
    }
    public void update(FriendRequest friendRequest){
        if(friendRequest == null){
            throw new ValidationException("Invalid request");
        }
        friendRequestRepo.update(friendRequest);
        if("pending".equals(friendRequest.getStatus())){
            lastFriendRequestSaved = friendRequest;
            notifyObservers(ChangeEvent.SENT_FRIEND_REQUEST);
            return;
        }
        notifyObservers(ChangeEvent.FRIEND_REQUEST_DATA);
    }

    public FriendRequest getLastFriendRequest() {
        return lastFriendRequestSaved;
    }

}
