package com.shakdwipeea.tuesday.home.notification;

import com.shakdwipeea.tuesday.data.entities.NotificationDetail;

/**
 * Created by ashak on 30-11-2016.
 */

public class NotificationContract {
    public interface NotificationView {
        void displayError(String reason);
        void displayProgressBar(boolean enable);

        void addRequestNotification(NotificationDetail notificationDetail);
        void clearRequestNotification();

        void addGrantedNotification(NotificationDetail notificationDetail);
        void clearGrantedNotification();
    }

    interface Presenter {
        void subscribe();
        void unSubscribe();
    }
}
