package com.shakdwipeea.tuesday.data.entities;

import com.shakdwipeea.tuesday.data.entities.user.Provider;
import com.shakdwipeea.tuesday.data.entities.user.User;

/**
 * Created by ashak on 30-11-2016.
 */

public class NotificationDetail {
    public User user;
    public Provider provider;

    public NotificationDetail() {
        this.user = new User();
        this.provider = new Provider();
    }
}
