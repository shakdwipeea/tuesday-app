package com.shakdwipeea.tuesday.auth.details;

import com.shakdwipeea.tuesday.data.entities.user.User;

/**
 * Created by akash on 20/12/16.
 */

public class DetailsContract {
    interface View {
        void displayProgressBar(Boolean enable);
        void displayError(String reason);
        void saveUserDetails(User user);
    }

    interface Presenter {
        void saveDetails(User user);
    }
}
