package utils.factories;

import enums.EventType;
import models.Event;
import models.RaceEvent;

public class EventFactory {
    private static EventFactory instance;
    private EventFactory(){}

    public static EventFactory getInstance(){
        if(instance == null)
            instance = new EventFactory();
        return instance;
    }
    public Event createEvent(String name, EventType type){
        if(type == EventType.RACE_EVENT)
            return new RaceEvent(name);
        return null;
    }

}
