package utils;

import models.FriendshipModel;
import models.UserModel;
import repo.DbFriendshipRepo;
import repo.DbUserRepo;

public class Models {
    private static final UserModel userModel = new UserModel(new DbUserRepo());
    private static final FriendshipModel friendshipModel = new FriendshipModel(new DbFriendshipRepo());

    public static UserModel getUserModel() {
        return userModel;
    }

    public static FriendshipModel getFriendshipModel() {
        return friendshipModel;
    }
}
