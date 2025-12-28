package utils.factories;

import domain.Duck;
import domain.Person;
import domain.User;
import utils.UserDTO;

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
