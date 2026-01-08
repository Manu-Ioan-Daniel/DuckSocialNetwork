package models;

public abstract class Event extends Entity<Long> {
    private final String name;
    public Event(String name){
        this.name = name;
    }
    public String getName(){
        return name;
    }

    public String getType() {
        if(this instanceof RaceEvent)
            return "Race Event";
        return "";
    }
}
