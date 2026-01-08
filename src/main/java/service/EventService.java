package service;


import enums.ChangeEvent;
import exceptions.ServiceException;
import models.*;
import repo.DbEventRepo;
import utils.dtos.EventSubscriberDTO;
import utils.factories.NotificationFactory;
import utils.observer.Observable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EventService extends Observable {
    private final UsersService usersService;
    private final DbEventRepo eventRepo;
    private final NotificationService notificationService;
    public EventService(UsersService usersService, NotificationService notificationService, DbEventRepo eventRepo) {
        this.usersService = usersService;
        this.eventRepo = eventRepo;
        this.notificationService = notificationService;
    }

    public List<Event> findAllEvents() {
        List<Event> events = new ArrayList<>();
        eventRepo.findAll().forEach(events::add);
        return events;
    }
    public Optional<User> findOneUser(Long id) {
        return usersService.findOne(id);
    }

    public void addEvent(Event ev) {
        eventRepo.save(ev);
        notifyObservers(ChangeEvent.EVENT_SAVED);
    }
    public void addSubscriber(Event event, User user) {
        if(event instanceof RaceEvent && user instanceof Duck)
            eventRepo.addSubscriber(event.getId(),user.getId());
        else{
            throw new ServiceException("Only ducks can register to race events!");
        }
        notifyObservers(ChangeEvent.USER_SUBSCRIBED);
    }

    public List<EventSubscriberDTO> getSubscribers(Long eventId){
        return eventRepo.getSubscribers(eventId);
    }


    public List<Notification> getNotifications(Long id) {
        return notificationService.getNotifications(id);
    }

    public void deleteNotification(Long id) {
        notificationService.delete(id);
    }


    public void sendMessageToSubscribers(String message, Event event) {
        List<EventSubscriberDTO> subscribers = getSubscribers(event.getId());
        for(EventSubscriberDTO subscriber : subscribers){
            notificationService.save(NotificationFactory.getInstance().createNotification(message,subscriber.getUserId()));
        }
    }
}
