package models;

import java.time.LocalDate;

public abstract class User extends Entity<Long> {
    private final String username;
    private final String email;
    private final String password;
    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getType() {
        return this instanceof Duck ? "duck" : "person";
    }
    public String getDescription(){
        if(this instanceof Duck d)
           return "Im just a " + d.getDuckType() + " duck with a speed of " + d.getSpeed() + " and a resistance of " + d.getResistance() + ", minding my own business";
        else if(this instanceof Person p){
            return "My name is " + p.getName() + " " + p.getSurname() + " and im just a " + (LocalDate.now().getYear() - p.getDateOfBirth().getYear()) + " year old person enjoying life as a " + p.getOccupation();
        }
        return "";
    }
}
