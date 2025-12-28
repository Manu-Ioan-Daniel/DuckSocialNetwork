package utils;

import service.ChatService;
import service.FriendsService;
import service.UsersService;

public class Services {
    private static final FriendsService friendsService = new FriendsService(Models.getUserModel(),Models.getFriendRequestModel(),Models.getFriendshipModel());
    private static final UsersService usersService = new UsersService(Models.getUserModel(),Models.getFriendshipModel(),Models.getFriendRequestModel());
    private static final ChatService chatService = new ChatService(Models.getUserModel(),Models.getMessageModel());
    public static FriendsService getFriendsService() {
        return friendsService;
    }
    public static UsersService getUsersService() {
        return usersService;
    }

    public static ChatService getChatService() { return chatService;}
}
