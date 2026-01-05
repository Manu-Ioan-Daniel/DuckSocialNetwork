package models;

public class RaceEvent extends Entity<Long> {
    private final String name;
    public RaceEvent(String name){
        this.name = name;
    }
    public String getName(){
        return name;
    }
}
