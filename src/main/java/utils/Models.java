package utils;

import models.FriendRequestModel;
import models.FriendshipModel;
import models.MessageModel;
import models.UserModel;
import repo.DbFriendRequestRepo;
import repo.DbFriendshipRepo;
import repo.DbMessageRepo;
import repo.DbUserRepo;

public class Models {
    private static final UserModel userModel = new UserModel(new DbUserRepo());
    private static final FriendshipModel friendshipModel = new FriendshipModel(new DbFriendshipRepo());
    private static final FriendRequestModel friendRequestModel = new FriendRequestModel(new DbFriendRequestRepo());
    private static final MessageModel messageModel = new MessageModel(new DbMessageRepo());

    public static UserModel getUserModel() {
        return userModel;
    }

    public static FriendshipModel getFriendshipModel() {
        return friendshipModel;
    }

    public static FriendRequestModel getFriendRequestModel() {
        return friendRequestModel;
    }
    public static MessageModel getMessageModel() {
        return messageModel;
    }
}
