package com.shakdwipeea.tuesday.auth;

import com.google.firebase.auth.FirebaseUser;

/**
 * Created by ashak on 02-10-2016.
 */

public class AuthContract {
    interface View {
        void openGoogleLogin();
        void openFacebookLogin();
        void openTwitterLogin();
        void displayError(String message);
        void openProfile(FirebaseUser user);
    }
}
