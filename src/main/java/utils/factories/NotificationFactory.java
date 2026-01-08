package utils.factories;


import models.Notification;

public class NotificationFactory {
    private static NotificationFactory instance;
    private NotificationFactory() {}
    public static NotificationFactory getInstance(){
        if (instance == null)
            instance = new NotificationFactory();
        return instance;
    }
    public Notification createNotification(String message, Long userId){
        return new Notification(message, userId);
    }
}
