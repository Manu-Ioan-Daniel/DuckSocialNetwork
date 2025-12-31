package utils.factories;

import models.Duck;
import models.Person;
import models.User;
import utils.dtos.UserDTO;

public class UserFactory {
    private static UserFactory instance;
    public static UserFactory getInstance(){
        if(instance == null){
            instance = new UserFactory();
        }
        return instance;
    }
    public User createUser(UserDTO input){

        return switch (input.getType()) {
            case "Duck" ->
                    new Duck(input.getUsername(), input.getEmail(), input.getPassword(), input.getDuckType(), input.getSpeed(), input.getResistance());
            case "Person" ->
                    new Person(input.getUsername(), input.getEmail(), input.getPassword(), input.getName(), input.getSurname(), input.getDateOfBirth(), input.getOccupation(), input.getEmpathyLevel());
            default->throw new IllegalArgumentException("Factory issue!");
        };
    }
}
