package com.shakdwipeea.tuesday.profile;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import com.shakdwipeea.tuesday.data.entities.Provider;
import com.shakdwipeea.tuesday.data.entities.User;

import java.util.List;

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

        void loggedInUser(boolean show);

        void openImageMenu();
        void setProgressBar(boolean show);

        void displayUser(User user);
        void setAddFriendFabIcon(Boolean value);

        void addProvider(List<Provider> provider);

        void clearProvider();
        void launchSetup();
        void displayProviderInfo(Provider provider, String providerDetails);
        void showAccessButton(boolean enable);

        Context getContext();
        boolean hasContactPermission();
    }

    /**
     * those actions which directly trigger an intent
     */
    interface IntentActions {
        void openPhotoPicker();
        void openCamera();
    }

    interface Presenter {
        void subscribe(User user);
        void loadProfile(User user);
        void updateProfilePic(String filePath);
        void updateProfilePic(Context context, Uri imageUri);
        void deleteProfilePic();
        void handleFab();
        void displayProviderDetails(Provider provider);
    }
}
