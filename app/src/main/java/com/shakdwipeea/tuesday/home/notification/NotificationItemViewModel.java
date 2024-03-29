package com.shakdwipeea.tuesday.home.notification;

import com.shakdwipeea.tuesday.data.entities.user.Provider;
import com.shakdwipeea.tuesday.data.entities.user.User;
import com.shakdwipeea.tuesday.data.firebase.UserService;

/**
 * Created by ashak on 01-12-2016.
 */

public class NotificationItemViewModel {
    private UserService userService;
    private NotificationAdapter notificationAdapter;
    private int position;

    public NotificationItemViewModel(NotificationAdapter notificationAdapter, int position) {
        this.notificationAdapter = notificationAdapter;
        this.position = position;
        userService = new UserService();
    }

    /**
     * Approve request
     *
     * @param approved Approve the request
     * @param user User for which to approve
     * @param provider Provider which to apprvoe
     */
    public void handleApproval(boolean approved, Provider provider, User user) {
        if (approved)
            userService.approveRequest(provider, user.uid);
        else
            userService.rejectRequest(provider, user.uid);

        notificationAdapter.removeItem(position);
    }

}
