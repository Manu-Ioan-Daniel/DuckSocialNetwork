package models;

import domain.Friendship;
import repo.DbFriendshipRepo;
import utils.Tuple;
import utils.observer.Observable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FriendshipModel extends Observable {
    private final DbFriendshipRepo friendshipRepo;
    public FriendshipModel(DbFriendshipRepo friendshipRepo) {
        this.friendshipRepo = friendshipRepo;
    }
    public Iterable<Friendship> findAll(){
        return friendshipRepo.findAll();
    }
    public Optional<Friendship> findOne(Tuple<Long,Long> id){
        return friendshipRepo.findOne(id);
    }
    public void save(Friendship friendship){
        friendshipRepo.save(friendship);
        notifyObservers();
    }
    public void delete(Tuple<Long,Long> id){
        friendshipRepo.delete(id);
        notifyObservers();
    }
    public void delete(Long id1,Long id2){
        friendshipRepo.delete(new Tuple<>(Long.min(id1,id2),Long.max(id1,id2)));
        notifyObservers();
    }
    public List<Long> findFriendsOf(Long id){
        List<Long> friendIds = new ArrayList<>();
        for(Long friendId:friendshipRepo.findFriendsOf(id)){
            friendIds.add(friendId);
        }
        return friendIds;
    }

}
