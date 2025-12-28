package utils.observer;

import enums.ChangeEvent;

public class NotificationHandler extends Observable implements Observer {

    private static NotificationHandler instance;

    private NotificationHandler() {}

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
