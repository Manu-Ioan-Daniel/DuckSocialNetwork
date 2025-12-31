package service;

import models.Friendship;
import enums.ChangeEvent;
import repo.DbFriendshipRepo;
import utils.Tuple;
import utils.observer.Observable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FriendshipService extends Observable {
    private final DbFriendshipRepo friendshipRepo;
    public FriendshipService(DbFriendshipRepo friendshipRepo) {
        this.friendshipRepo = friendshipRepo;
    }

    public Optional<Friendship> findOne(Tuple<Long,Long> id){
        return friendshipRepo.findOne(id);
    }

    public void save(Friendship friendship){
        friendshipRepo.save(friendship);
        notifyObservers(ChangeEvent.FRIENDSHIP_DATA);
    }

    public void delete(Long id1,Long id2){
        friendshipRepo.delete(new Tuple<>(Long.min(id1,id2),Long.max(id1,id2)));
        notifyObservers(ChangeEvent.FRIENDSHIP_DATA);
    }

    public List<Long> findFriendsOf(Long id){
        List<Long> friendIds = new ArrayList<>();
        for(Long friendId:friendshipRepo.findFriendsOf(id)){
            friendIds.add(friendId);
        }
        return friendIds;
    }

}
