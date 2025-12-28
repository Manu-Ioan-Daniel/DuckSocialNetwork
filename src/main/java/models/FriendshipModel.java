package models;

import domain.Friendship;
import enums.ChangeEvent;
import repo.DbFriendshipRepo;
import utils.Tuple;
import utils.observer.NotificationHandler;
import utils.observer.Observable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FriendshipModel extends Observable {
    private final DbFriendshipRepo friendshipRepo;
    public FriendshipModel(DbFriendshipRepo friendshipRepo) {

        this.friendshipRepo = friendshipRepo;
        this.addObserver(NotificationHandler.getInstance());
    }
    public Iterable<Friendship> findAll(){
        return friendshipRepo.findAll();
    }

    public Optional<Friendship> findOne(Tuple<Long,Long> id){
        return friendshipRepo.findOne(id);
    }

    public void save(Friendship friendship){
        friendshipRepo.save(friendship);
        notifyObservers(ChangeEvent.FRIENDSHIP_DATA);
    }

    public void delete(Tuple<Long,Long> id){
        friendshipRepo.delete(id);
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
