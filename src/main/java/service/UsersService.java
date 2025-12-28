package service;

import domain.Duck;
import domain.Entity;
import domain.FriendRequest;
import domain.User;
import enums.ChangeEvent;
import javafx.collections.ObservableList;
import models.FriendRequestModel;
import models.FriendshipModel;
import models.UserModel;
import utils.observer.Observable;
import utils.observer.Observer;

import java.util.Optional;

public class UsersService{
    private final UserModel userModel;
    private final FriendshipModel friendshipModel;
    private final FriendRequestModel friendRequestModel;

    public UsersService(UserModel userModel, FriendshipModel friendshipModel, FriendRequestModel friendRequestModel) {

        this.userModel = userModel;
        this.friendshipModel = friendshipModel;
        this.friendRequestModel = friendRequestModel;

    }


    public Optional<User> getUser(String username) {
        return userModel.findOne(username);
    }

    public String getFriendsToString(Long userId) {
        return userModel.mapIdsToUsernamesString(friendshipModel.findFriendsOf(userId));
    }

    public String getType(User user) {
        return user instanceof Duck ? "duck" : "person";
    }

    public int getTotalUsers() {
        return userModel.getTotalUsers();
    }

    public int getTotalDucks() {
        return userModel.getTotalDucks();
    }

    public int getTotalPeople() {
        return userModel.getTotalPeople();
    }

    public int getPageCount(int usersPerPage) {
        return userModel.getPageCount(usersPerPage);
    }

    public ObservableList<User> findUsersFromPage(int pageIndex, int usersPerPage) {
        return userModel.findUsersFromPage(pageIndex, usersPerPage);
    }

    public void delete(Long id) {
        userModel.delete(id);
    }

    public String getFriendRequestsToString(Long id) {
        StringBuilder stringBuilder = new StringBuilder();
        friendRequestModel.findFriendRequestsOf(id).forEach(friendRequest -> {
            userModel.findOne(friendRequest.getId().getFirst()).ifPresent(user -> {
                stringBuilder.append(user.getUsername()).append("\n");
            });
        });
        return stringBuilder.toString();
    }

    public FriendRequest getLastFriendRequest() {
        return friendRequestModel.getLastFriendRequest();
    }

}
