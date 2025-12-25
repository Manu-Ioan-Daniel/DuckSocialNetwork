package service;

import domain.FriendRequest;
import domain.User;
import javafx.collections.ObservableList;
import models.FriendRequestModel;
import models.FriendshipModel;
import models.UserModel;
import utils.FriendRequestFactory;
import utils.observer.Observable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FriendsService extends Observable {
    private final UserModel userModel;
    private final FriendRequestModel friendRequestModel;
    private final FriendshipModel friendshipModel;

    public FriendsService(UserModel userModel, FriendRequestModel friendRequestModel, FriendshipModel friendshipModel) {
        this.userModel = userModel;
        this.friendRequestModel = friendRequestModel;
        this.friendshipModel = friendshipModel;
    }

    public Optional<User> getUser(String username){
        return userModel.findOne(username);
    }

    public String getToUsername(FriendRequest fr) {
        return userModel.findOne(fr.getId().getSecond()).map(User::getUsername).orElse("");
    }

    public String getFromUsername(FriendRequest fr) {
        return userModel.findOne(fr.getId().getFirst()).map(User::getUsername).orElse("");
    }

    public ObservableList<User> getFriends(Long currentUserId) {
        return userModel.mapIdsToUsers(friendshipModel.findFriendsOf(currentUserId));
    }

    public ObservableList<User> getOthers(Long currentUserId) {
        List<Long> ids = new ArrayList<>(friendshipModel.findFriendsOf(currentUserId));
        ids.add(currentUserId);
        for(FriendRequest fr : friendRequestModel.findSentFriendRequests(currentUserId)){
            ids.add(fr.getId().getSecond());
        }
        for(FriendRequest fr : friendRequestModel.findFriendRequestsOf(currentUserId)){
            ids.add(fr.getId().getFirst());
        }
        return userModel.getAllUsersExcept(ids);
    }

    public ObservableList<FriendRequest> findSentFriendRequests(Long currentUserId) {
        return friendRequestModel.findSentFriendRequests(currentUserId);
    }

    public ObservableList<FriendRequest> findReceivedFriendRequests(Long currentUserId) {
        return friendRequestModel.findFriendRequestsOf(currentUserId);
    }

    public void deleteFriend(Long currentUserId, Long friendId) {
        friendshipModel.delete(currentUserId, friendId);
        notifyObservers();
    }

    public void saveFriendRequest(Long currentUserId, Long targetId) {
        friendRequestModel.save(FriendRequestFactory.getInstance().createFriendRequest(currentUserId, targetId, "pending"));
        notifyObservers();
    }
}