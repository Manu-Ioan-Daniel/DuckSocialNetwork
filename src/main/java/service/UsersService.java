package service;

import exceptions.ValidationException;
import models.User;
import enums.ChangeEvent;
import repo.DbUserRepo;
import utils.observer.Observable;
import validation.Validator;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;


public class UsersService extends Observable {
    private final DbUserRepo userRepo;
    private final Validator<User> userValidator;

    public UsersService(DbUserRepo repo, Validator<User> userValidator) {
        this.userRepo=repo;
        this.userValidator = userValidator;
    }
    public Optional<User> findOne(Long id) {

        if(id==null || id<0){
            throw new ValidationException("Invalid id!");
        }
        return userRepo.findOne(id);
    }

    public Optional<User> findOne(String username) {

        if(username==null || username.isEmpty()){
            throw new ValidationException("Invalid username!");
        }
        return userRepo.findOne(username);
    }

    public List<User> findUsersFromPage(int page, int pageSize){
        if(page<0 || pageSize<=0){
            throw new ValidationException("Invalid page!");
        }
        int offset = (page) * pageSize;
        List<User> users = new ArrayList<>();
        userRepo.findUsersFromPage(offset, pageSize).forEach(users::add);
        return users;
    }


    public List<User> mapIdsToUsers(List<Long> ids){
        if(ids==null || ids.isEmpty()){
            return new ArrayList<>();
        }
        return ids.stream()
                .map(id->userRepo.findOne(id).orElseThrow(()->new ValidationException("User with id:  " + id + "does not exist!")))
                .toList();
    }

    public List<User> getAllUsersExcept(Set<Long> exceptIds){
        if(exceptIds==null || exceptIds.isEmpty()){
            return new ArrayList<>();
        }
        return findAll().stream()
                .filter(u->!exceptIds.contains(u.getId()))
                .toList();
    }

    public List<User> findAll() {
       List<User> users = new ArrayList<>();
       userRepo.findAll().forEach(users::add);
       return users;
    }

    public void save(User user) {
        userValidator.validate(user);
        userRepo.save(user);
        notifyObservers(ChangeEvent.USER_DATA);
    }

    public void delete(Long id) {
        if(id==null || id<0){
            throw new ValidationException("Invalid id!");
        }
        userRepo.delete(id);
        notifyObservers(ChangeEvent.USER_DATA);
    }

    public int getPageCount(int usersPerPage){
        if(usersPerPage<=0){
            throw new  ValidationException("Invalid usersPerPage!");
        }
        int totalUsers = getTotalUsers();
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
