package service;

import enums.ChangeEvent;
import models.Notification;
import repo.DbNotificationRepo;
import utils.observer.Observable;

import java.util.ArrayList;
import java.util.List;

public class NotificationService extends Observable {
    private final DbNotificationRepo repo;
    public NotificationService(DbNotificationRepo repo) {
        this.repo = repo;
    }

    public void save(Notification notification){
        repo.save(notification);
        notifyObservers(ChangeEvent.NOTIFICATION);
    }

    public void delete(Long id) {
        repo.delete(id);
    }

    public List<Notification> getNotifications(Long id) {
        List<Notification> notifications = new ArrayList<>();
        repo.findUserNotifications(id).forEach(notifications::add);
        return notifications;
    }


}
