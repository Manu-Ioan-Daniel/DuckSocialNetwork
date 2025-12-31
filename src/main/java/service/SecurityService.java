package service;

import models.User;
import utils.passwords.PasswordHasher;

import java.util.Optional;

public class SecurityService {
    private final UsersService usersService;

    public SecurityService(UsersService usersService) {
        this.usersService = usersService;
    }

    public boolean validLogin(String username,String password){
        Optional<User> user = usersService.findOne(username);
        if(user.isEmpty()){
            return false;
        }
        try {
            return PasswordHasher.verifyPassword(password, user.get().getPassword());
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }
}
