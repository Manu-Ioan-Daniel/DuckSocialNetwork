package utils.observer;

import enums.ChangeEvent;
import utils.Services;

public class NotificationHandler extends Observable implements Observer {

    private static NotificationHandler instance;

    private NotificationHandler() {
        Services.getUsersService().addObserver(this);
        Services.getFriendshipService().addObserver(this);
        Services.getFriendRequestService().addObserver(this);
        Services.getMessageService().addObserver(this);
        Services.getEventService().addObserver(this);
        Services.getNotificationService().addObserver(this);
    }

    public static NotificationHandler getInstance() {
        if (instance == null)
            instance = new NotificationHandler();
        return instance;
    }

    @Override
    public void update(ChangeEvent event) {
        notifyObservers(event);
    }
}
