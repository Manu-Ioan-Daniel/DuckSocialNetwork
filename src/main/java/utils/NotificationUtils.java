package utils;

import models.Notification;

import java.util.List;
import java.util.function.Consumer;

public final class NotificationUtils {

    private NotificationUtils() {}

    public static String getAndClearNotifications(List<Notification> notifications, Consumer<Long> remover) {
        if (notifications.isEmpty())
            return null;

        StringBuilder sb = new StringBuilder();
        for (Notification notification : notifications) {
            sb.append(notification.getMessage()).append("\n");
            remover.accept(notification.getId());
        }
        return sb.toString();
    }
}