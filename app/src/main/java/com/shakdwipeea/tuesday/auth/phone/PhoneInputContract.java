package com.shakdwipeea.tuesday.auth.phone;

/**
 * Created by akash on 19/12/16.
 */

public class PhoneInputContract {
    interface View {
        void displayError(String message);
        void launchOtpView();
        void launchHomeView();
    }

    interface Presenter {
        void getAccountDetails(String phoneNumber);
    }
}
