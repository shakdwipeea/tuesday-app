package com.shakdwipeea.tuesday.profile;

import android.content.Context;
import android.net.Uri;

import com.shakdwipeea.tuesday.data.entities.user.Provider;
import com.shakdwipeea.tuesday.data.entities.user.ProviderDetails;
import com.shakdwipeea.tuesday.data.entities.user.User;
import com.shakdwipeea.tuesday.picture.ProfilePictureView;

import java.util.List;

/**
 * Created by ashak on 15-10-2016.
 */

public interface ProfileContract {
    interface View extends ProfilePictureView {
        void displayName(String name);

        void loggedInUser(boolean show);
        void displayUser(User user);

        void setAddFriendFabIcon(Boolean value);
        void addProvider(List<Provider> provider);

        void addCallDetails(ProviderDetails callDetails);
        void clearCallDetails();

        void addMailDetails(ProviderDetails mailDetails);
        void clearMailDetails();

        void launchSetup();
        void displayProviderInfo(Provider provider, String providerDetails);
        void showAccessButton(boolean enable);
    }

    interface Presenter {
        void subscribe(User user);
        void loadProfile(User user);
        void updateProfilePic(String filePath);
        void updateProfilePic(Context context, Uri imageUri);
        void deleteProfilePic();
        void handleFab();
        void displayProviderDetails(Provider provider);
        void toggleContact();
        void changeName(String name);
    }
}
