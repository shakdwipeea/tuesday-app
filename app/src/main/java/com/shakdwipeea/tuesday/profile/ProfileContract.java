package com.shakdwipeea.tuesday.profile;

import android.graphics.Bitmap;

import java.io.InputStream;

/**
 * Created by ashak on 15-10-2016.
 */

public interface ProfileContract {
    interface View {
        void displayError(String error);
        void displayProfilePic(String url);
        void displayProfilePic(Bitmap image);
        void displayName(String name);
        void openImageMenu();
        void launchAuth();
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
        void logout();
        void updateProfilePic(InputStream imageStream);
    }
}
