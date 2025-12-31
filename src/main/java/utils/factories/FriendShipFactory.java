package utils.factories;
import models.Friendship;
import utils.Tuple;

public class FriendShipFactory {
    private static FriendShipFactory instance;
    private FriendShipFactory() {}
    public static FriendShipFactory getInstance() {
        if (instance == null) {
            instance = new FriendShipFactory();
        }
        return instance;
    }
    public Friendship createFriendShip(Long id1, Long id2){
        Friendship friendship = new Friendship();
        friendship.setId(new Tuple<>(Long.min(id1,id2),Long.max(id1,id2)));
        return friendship;
    }

}
