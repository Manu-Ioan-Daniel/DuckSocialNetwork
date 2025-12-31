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

    public Optional<FriendRequest> findOne(Long id1, Long id2){
        if(id1 == null || id2 == null || id1<0 || id2<0){
            throw new ValidationException("Invalid ids");
        }
        return friendRequestRepo.findOne(new Tuple<>(id1,id2));
    }


    public List<FriendRequest> findFriendRequestsOf(Long id){
        if(id == null || id<0){
            throw new ValidationException("Invalid ids");
        }
        List<FriendRequest> list = new ArrayList<>();
        friendRequestRepo.findFriendRequestsOf(id).forEach(list::add);
        return list;
    }

    public List<FriendRequest> findSentFriendRequests(Long id){
        List<FriendRequest> list = new ArrayList<>();
        friendRequestRepo.findSentFriendRequests(id).forEach(list::add);
        return list;
    }

    public void save(FriendRequest friendRequest){
        friendRequestRepo.save(friendRequest);
        lastFriendRequestSaved = friendRequest;
        notifyObservers(ChangeEvent.SENT_FRIEND_REQUEST);
    }
    public void delete(Long id1, Long id2){
        if(id1 == null || id1<0 || id2 == null || id2<0){
            throw new ValidationException("Invalid ids!");
        }
        friendRequestRepo.delete(new Tuple<>(id1,id2));
        notifyObservers(ChangeEvent.FRIEND_REQUEST_DATA);
    }
    public void update(FriendRequest friendRequest){
        friendRequestRepo.update(friendRequest);
        if(friendRequest.getStatus().equals("pending")){
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
