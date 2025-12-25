package utils;

import service.FriendsService;

public class Services {
    private static final FriendsService friendsService = new FriendsService(Models.getUserModel(),Models.getFriendRequestModel(),Models.getFriendshipModel());
    public static FriendsService getFriendsService() {
        return friendsService;
    }
}
