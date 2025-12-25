package models;
import domain.FriendRequest;
import enums.ChangeEvent;
import exceptions.ValidationException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import repo.DbFriendRequestRepo;
import utils.Tuple;
import utils.observer.Observable;

import java.util.Optional;


public class FriendRequestModel extends Observable {

    private final DbFriendRequestRepo friendRequestRepo;
    private FriendRequest lastFriendRequestSaved;

    public FriendRequestModel(DbFriendRequestRepo friendRequestRepo) {
        this.friendRequestRepo = friendRequestRepo;
    }

    public Optional<FriendRequest> findOne(Long id1, Long id2){
        return friendRequestRepo.findOne(new Tuple<>(id1,id2));
    }
    public ObservableList<FriendRequest> findAll(){
        ObservableList<FriendRequest> list = FXCollections.observableArrayList();
        for(FriendRequest fr:friendRequestRepo.findAll()){
            list.add(fr);
        }
        return list;
    }

    public ObservableList<FriendRequest> findFriendRequestsOf(Long id){
        ObservableList<FriendRequest> list = FXCollections.observableArrayList();
        for(FriendRequest fr:friendRequestRepo.findFriendRequestsOf(id)){
            list.add(fr);
        }
        return list;
    }

    public ObservableList<FriendRequest> findSentFriendRequests(Long id){
        ObservableList<FriendRequest> list = FXCollections.observableArrayList();
        for(FriendRequest fr:friendRequestRepo.findSentFriendRequests(id)){
            list.add(fr);
        }
        return list;
    }

    public void save(FriendRequest friendRequest){
        friendRequestRepo.save(friendRequest);
        notifyObservers(ChangeEvent.SENT_FRIEND_REQUEST);
    }
    public void delete(Long id1, Long id2){
        if(id1<0 || id2<0){
            throw new ValidationException("Id's cannot be negative");
        }
        friendRequestRepo.delete(new Tuple<>(id1,id2));
        notifyObservers(ChangeEvent.FRIEND_REQUEST_DATA);
    }
    public void update(FriendRequest friendRequest){
        friendRequestRepo.update(friendRequest);
        notifyObservers(ChangeEvent.USER_DATA);
    }

    public FriendRequest getLastFriendRequest() {
        return lastFriendRequestSaved;
    }

    public void setLastFriendRequestSaved(FriendRequest lastFriendRequestSaved) {
        this.lastFriendRequestSaved = lastFriendRequestSaved;
    }
}
