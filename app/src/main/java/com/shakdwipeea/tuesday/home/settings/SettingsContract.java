package com.shakdwipeea.tuesday.home.settings;

import com.shakdwipeea.tuesday.data.entities.user.User;

/**
 * Created by ashak on 27-11-2016.
 */

public interface SettingsContract {
    interface View {
        void displayError(String reason);
        void displayUserDetails(User user);
        void showProgress(boolean enable);
        void launchAuth();
    }

    interface Presenter {
        void getUser();
    }
}
