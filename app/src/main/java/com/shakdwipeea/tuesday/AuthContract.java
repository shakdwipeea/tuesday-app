package com.shakdwipeea.tuesday;

/**
 * Created by ashak on 02-10-2016.
 */

public class AuthContract {
    interface View {
        void openGoogleLogin();
        void displayError(String message);
    }
}
