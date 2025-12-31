package utils.dtos;

import models.User;

import java.util.List;
import java.util.stream.Collectors;

public class UserTableDTO {
    private final Long id;
    private final String username;
    private final String email;
    private final String type;
    private final String friendsString;

    public UserTableDTO(User user, List<User> friends) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.type = user.getType();
        this.friendsString = friends.stream()
                .map(User::getUsername)
                .collect(Collectors.joining(","));
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getType() {
        return type;
    }

    public String getFriendsString() {
        return friendsString;
    }
}
