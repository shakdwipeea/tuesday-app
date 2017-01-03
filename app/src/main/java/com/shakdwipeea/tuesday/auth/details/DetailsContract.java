package com.shakdwipeea.tuesday.auth.details;

import com.google.firebase.auth.FirebaseUser;
import com.shakdwipeea.tuesday.data.entities.user.User;
import com.shakdwipeea.tuesday.picture.ProfilePictureView;

/**
 * Created by akash on 20/12/16.
 */

public class DetailsContract {
    interface View extends ProfilePictureView {
        void openProfile(FirebaseUser user);
        void displayName(String name);
    }

    interface Presenter {
        void saveDetails(User user);
    }
}
