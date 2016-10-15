package com.shakdwipeea.tuesday.profile;

/**
 * Created by ashak on 15-10-2016.
 */

public interface ProfileContract {
    interface View {
        void displayError(String error);
        void displayProfilePic(String url);
        void displayName(String name);
    }

    interface Presenter {
        void subscribe();
    }
}
