package com.shakdwipeea.tuesday.home.notification;

import com.shakdwipeea.tuesday.data.entities.NotificationDetail;

/**
 * Created by ashak on 30-11-2016.
 */

public class NotificationContract {
    interface View {
        void displayError(String reason);
        void addNotification(NotificationDetail notificationDetail);
        void clearNotification();
        void displayProgressBar(boolean enable);
    }

    interface Presenter {
        void subscribe();
        void unSubscribe();
    }
}
