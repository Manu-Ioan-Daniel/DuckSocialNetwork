package service;

import models.FriendRequest;
import models.User;
import exceptions.ServiceException;
import utils.dtos.UserTableDTO;
import utils.factories.FriendRequestFactory;
import utils.factories.FriendShipFactory;
import java.util.*;

public class CommunityService {
    private final UsersService usersService;
    private final FriendRequestService friendRequestService;
    private final FriendshipService friendshipService;

    public CommunityService(UsersService usersService, FriendRequestService friendRequestService, FriendshipService friendshipService) {
        this.usersService = usersService;
        this.friendRequestService = friendRequestService;
        this.friendshipService = friendshipService;
    }

    public Optional<User> findUser(String username){
        return usersService.findOne(username);
    }

    public List<UserTableDTO> findUsersFromPageAsDTO(int pageIndex, int usersPerPage) {
        List<User> users = usersService.findUsersFromPage(pageIndex, usersPerPage);
        return users.stream()
                .map(u->new UserTableDTO(u,getFriends(u.getId())))
                .toList();
    }

    public List<FriendRequest> findSentFriendRequests(Long currentUserId) {
        return friendRequestService.findSentFriendRequests(currentUserId);
    }

    public List<FriendRequest> findReceivedFriendRequests(Long currentUserId) {
        return friendRequestService.findFriendRequestsOf(currentUserId);
    }


    public int getTotalUsers() {
        return usersService.getTotalUsers();
    }
    public int getTotalDucks() {
        return usersService.getTotalDucks();
    }
    public int getTotalPeople(){
        return usersService.getTotalPeople();
    }

    public int getPageCount(int usersPerPage) {
        return usersService.getPageCount(usersPerPage);
    }

    public List<User> getFriends(Long id){

        List<Long> friendIds = friendshipService.findFriendsOf(id);
        return usersService.mapIdsToUsers(friendIds);
    }

    public void deleteUser(Long id) {
        usersService.delete(id);
    }

    public void deleteFriend(Long currentUserId, Long friendId) {
        friendshipService.delete(currentUserId, friendId);
    }

    public void deleteFriendRequest(Long id1, Long id2) {
        friendRequestService.delete(id1, id2);
    }

    public void cancelFriendRequest(FriendRequest fr) {
        if(!fr.getStatus().equals("pending")){
            throw new ServiceException("You cannot cancel friend requests that are not pending!");
        }
        friendRequestService.delete(fr.getId().getSecond(), fr.getId().getFirst());
    }

    public String getToUsername(FriendRequest fr) {
        return usersService.findOne(fr.getId().getSecond()).map(User::getUsername).orElse("");
    }

    public String getFromUsername(FriendRequest fr) {
        return usersService.findOne(fr.getId().getFirst()).map(User::getUsername).orElse("");
    }

    public List<User> getOthers(Long currentUserId) {
        Set<Long> ids = new HashSet<>(friendshipService.findFriendsOf(currentUserId));
        ids.add(currentUserId);
        for(FriendRequest fr : friendRequestService.findSentFriendRequests(currentUserId)){
            if(fr.getStatus().equals("pending"))
                ids.add(fr.getId().getSecond());
        }
        for(FriendRequest fr : friendRequestService.findFriendRequestsOf(currentUserId)){
            if(fr.getStatus().equals("pending"))
                ids.add(fr.getId().getFirst());
        }
        return usersService.getAllUsersExcept(ids);
    }


    public FriendRequest getLastFriendRequest() {
        return friendRequestService.getLastFriendRequest();
    }


    public void saveFriendRequest(Long currentUserId, Long targetId) {

        if(currentUserId.equals(targetId)){
            throw new ServiceException("You cannot friend reuqest yourself!");
        }

        Optional<FriendRequest> fr = friendRequestService.findOne(currentUserId, targetId);
        if(fr.isEmpty()){
            friendRequestService.save(FriendRequestFactory.getInstance().createFriendRequest(currentUserId, targetId, "pending"));
            return;
        }
        FriendRequest friendRequest = fr.get();

        if(friendRequest.getStatus().equals("pending") || friendRequest.getStatus().equals("accepted"))
            return;
        if(friendRequest.getId().getFirst().equals(currentUserId)) {
            friendRequest.setStatus("pending");
            friendRequestService.update(friendRequest);
            return;
        }
        friendRequestService.delete(currentUserId, targetId);
        friendRequestService.save(FriendRequestFactory.getInstance().createFriendRequest(currentUserId, targetId, "pending"));

    }


    public void acceptFriendRequest(FriendRequest fr) {
        if(!fr.getStatus().equals("pending")){
            throw new ServiceException("You cannot accept friend requests that are not pending!");
        }
        fr.setStatus("accepted");
        friendRequestService.update(fr);
        friendshipService.save(FriendShipFactory.getInstance().createFriendShip(fr.getId().getFirst(), fr.getId().getSecond()));
    }

    public void denyFriendRequest(FriendRequest fr) {
        if(!fr.getStatus().equals("pending")){
            throw new ServiceException("You cannot deny friend requests that are not pending!");
        }
        fr.setStatus("denied");
        friendRequestService.update(fr);
    }

    public Optional<FriendRequest> findFriendRequest(Long id1, Long id2) {
        return friendRequestService.findOne(id1, id2);
    }
}