package utils;

import models.FriendRequestModel;
import models.FriendshipModel;
import models.UserModel;
import repo.DbFriendRequestRepo;
import repo.DbFriendshipRepo;
import repo.DbUserRepo;

public class Models {
    private static final UserModel userModel = new UserModel(new DbUserRepo());
    private static final FriendshipModel friendshipModel = new FriendshipModel(new DbFriendshipRepo());
    private static final FriendRequestModel friendRequestModel = new FriendRequestModel(new DbFriendRequestRepo());

    public static UserModel getUserModel() {
        return userModel;
    }

    public static FriendshipModel getFriendshipModel() {
        return friendshipModel;
    }

    public static FriendRequestModel getFriendRequestModel() {
        return friendRequestModel;
    }
}
