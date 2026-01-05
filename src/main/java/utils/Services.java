package utils;

import repo.*;
import service.*;
import validation.IdValidator;
import validation.UserValidator;

public class Services {
    private static final FriendshipService friendshipService = new FriendshipService(new DbFriendshipRepo(),new IdValidator());
    private static final UsersService usersService = new UsersService(new DbUserRepo(),new UserValidator(),new IdValidator());
    private static final SecurityService securityService = new SecurityService(usersService);
    private static final FriendRequestService friendRequestService = new FriendRequestService(new DbFriendRequestRepo(),new IdValidator());
    private static final MessageService messageService = new MessageService(new DbMessageRepo(),new IdValidator());
    private static final ChatService chatService = new ChatService(usersService,messageService);
    private static final CommunityService communityService = new CommunityService(usersService,friendRequestService,friendshipService);
    private static final EventService eventService = new EventService(usersService,new DbEventRepo());

    public static UsersService getUsersService() {
        return usersService;
    }

    public static FriendshipService getFriendshipService() {
        return friendshipService;
    }

    public static FriendRequestService getFriendRequestService() {
        return friendRequestService;
    }

    public static CommunityService getCommunityService() {
        return communityService;
    }

    public static SecurityService getSecurityService() {
        return securityService;
    }

    public static MessageService getMessageService() {
        return messageService;
    }

    public static ChatService getChatService() { return chatService;}

    public static EventService getEventService() { return eventService;}

}
