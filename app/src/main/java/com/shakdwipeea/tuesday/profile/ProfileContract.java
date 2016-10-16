package com.shakdwipeea.tuesday.profile;

/**
 * Created by ashak on 15-10-2016.
 */

public interface ProfileContract {
    interface View {
        void displayError(String error);
        void displayProfilePic(String url);
        void displayName(String name);
        void openImageMenu();
    }

    /**
     * those actions which directly trigger an intent
     */
    interface IntentActions {
        void openPhotoPicker();
        void openCamera();
    }

    interface Presenter {
        void subscribe();
        void changeProfilePic();
    }
}
