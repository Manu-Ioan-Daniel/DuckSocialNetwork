package service;

import domain.FriendRequest;
import domain.User;
import exceptions.ServiceException;
import javafx.collections.ObservableList;
import models.FriendRequestModel;
import models.FriendshipModel;
import models.UserModel;
import utils.factories.FriendRequestFactory;
import utils.factories.FriendShipFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FriendsService{
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
            if(fr.getStatus().equals("pending"))
                ids.add(fr.getId().getSecond());
        }
        for(FriendRequest fr : friendRequestModel.findFriendRequestsOf(currentUserId)){
            if(fr.getStatus().equals("pending"))
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
    }

    public void saveFriendRequest(Long currentUserId, Long targetId) {
        Optional<FriendRequest> fr = friendRequestModel.findOne(currentUserId, targetId);
        FriendRequest fr2 = FriendRequestFactory.getInstance().createFriendRequest(currentUserId, targetId, "pending");
        if(fr.isPresent() && !fr.get().getStatus().equals("pending")) {
            if(fr.get().getId().getFirst().equals(currentUserId)) {
                friendRequestModel.update(fr2);
                return;
            }
            friendRequestModel.delete(currentUserId, targetId);
            friendRequestModel.save(fr2);
            return;
        }
        friendRequestModel.save(FriendRequestFactory.getInstance().createFriendRequest(currentUserId, targetId, "pending"));
    }

    public void cancelFriendRequest(FriendRequest fr) {
        if(!fr.getStatus().equals("pending")){
            throw new ServiceException("You cannot cancel friend requests that are not pending!");
        }
        friendRequestModel.delete(fr.getId().getSecond(), fr.getId().getFirst());
    }

    public void acceptFriendRequest(FriendRequest fr) {
        if(!fr.getStatus().equals("pending")){
            throw new ServiceException("You cannot accept friend requests that are not pending!");
        }
        fr.setStatus("accepted");
        friendRequestModel.update(fr);
        friendshipModel.save(FriendShipFactory.getInstance().createFriendShip(fr.getId().getFirst(), fr.getId().getSecond()));
    }

    public void denyFriendRequest(FriendRequest fr) {
        if(!fr.getStatus().equals("pending")){
            throw new ServiceException("You cannot deny friend requests that are not pending!");
        }
        fr.setStatus("denied");
        friendRequestModel.update(fr);
    }
    public void deleteFriendRequest(Long id1, Long id2) {
        friendRequestModel.delete(id1, id2);
    }
}