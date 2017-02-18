package com.shakdwipeea.tuesday.auth.otp;

/**
 * Created by akash on 20/12/16.
 */

public interface OtpContract {
    interface View {
        void displayProgressbar(boolean enable);
        void displayError(String message);
        void launchDetailsInputView(String token);
    }

    interface Presenter {
        void verifyOtp(String otp, String phoneNumber);
    }
}
