package models;

import domain.User;
import enums.ChangeEvent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import repo.DbUserRepo;
import utils.observer.Observable;
import utils.passwords.PasswordHasher;
import validation.UserValidator;
import validation.Validator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public class UserModel extends Observable {
    private final DbUserRepo userRepo;
    private final Validator<User> userValidator;

    public UserModel(DbUserRepo repo) {
        this.userRepo=repo;
        this.userValidator = new UserValidator();
    }
    public Optional<User> findOne(Long id) {
        return userRepo.findOne(id);
    }

    public Optional<User> findOne(String username) {
        return userRepo.findOne(username);
    }

    public ObservableList<User> findUsersFromPage(int page, int pageSize){
        int offset = (page) * pageSize;
        ObservableList<User> users = FXCollections.observableArrayList();
        for(User user: userRepo.findUsersFromPage(offset, pageSize)){
            users.add(user);
        }
        return users;
    }

    public String mapIdsToUsernamesString(List<Long> ids) {
        return ids.stream()
                .map(this::findOne)          // returns Optional<User>
                .filter(Optional::isPresent)
                .map(optUser -> optUser.get().getUsername())
                .collect(Collectors.joining(", "));
    }

    public ObservableList<User> mapIdsToUsers(List<Long> ids){
        return FXCollections.observableArrayList(ids.stream()
                .map(this::findOne)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList());
    }

    public ObservableList<User> getAllUsersExcept(List<Long> exceptIds){
        return FXCollections.observableArrayList(findAll().stream()
                .filter(u->!exceptIds.contains(u.getId()))
                .toList());
    }

    public ObservableList<User> findAll() {
        ObservableList<User> users = FXCollections.observableArrayList();
        for(User user : userRepo.findAll()){
            users.add(user);
        }
        return users;
    }

    public void save(User user) {
        userValidator.validate(user);
        userRepo.save(user);
        notifyObservers(ChangeEvent.USER_DATA);
    }

    public void delete(Long id) {
       userRepo.delete(id);
       notifyObservers(ChangeEvent.USER_DATA);
    }

    public boolean validLogin(String username,String password){
        Optional<User> user = findOne(username);
        if(user.isEmpty()){
            return false;
        }
        try {
            return PasswordHasher.verifyPassword(password, user.get().getPassword());
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    public int getPageCount(int usersPerPage){
        int totalUsers =getTotalUsers();
        return totalUsers/usersPerPage + (totalUsers % usersPerPage == 0 ? 0 : 1);
    }

    public int getTotalUsers(){
        return userRepo.countUsers();
    }
    public int getTotalDucks(){
        return userRepo.countDucks();
    }
    public int getTotalPeople(){
        return userRepo.countPeople();
    }

}
