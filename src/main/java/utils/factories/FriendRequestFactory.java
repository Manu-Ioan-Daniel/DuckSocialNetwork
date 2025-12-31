package utils.factories;

import models.FriendRequest;
import utils.Tuple;

public class FriendRequestFactory {
    private static FriendRequestFactory instance;
    public FriendRequest createFriendRequest(Long id1,Long id2,String status){
        FriendRequest fr = new FriendRequest(status);
        fr.setId(new Tuple<>(id1,id2));
        return fr;
    }
    public static FriendRequestFactory getInstance(){
        if(instance == null){
            instance = new FriendRequestFactory();
        }
        return instance;
    }

}
