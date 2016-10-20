package com.shakdwipeea.tuesday.profile;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

/**
 * Created by ashak on 15-10-2016.
 */

public interface ProfileContract {
    interface View {
        void displayError(String error);

        void displayProfilePic(String url);
        void displayProfilePic(Bitmap bitmap);
        void displayProfilePicFromPath(String imagePath);
        void displayDefaultPic();

        void displayName(String name);
        void openImageMenu();
        void setProgressBar(boolean show);
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
        void updateProfilePic(String filePath);
        void updateProfilePic(Context context, Uri imageUri);
        void deleteProfilePic();
    }
}