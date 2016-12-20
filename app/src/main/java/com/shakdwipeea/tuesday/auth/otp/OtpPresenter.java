package com.shakdwipeea.tuesday.auth.otp;

import android.text.TextUtils;

import com.google.firebase.auth.FirebaseAuth;
import com.shakdwipeea.tuesday.data.api.ApiFactory;
import com.shakdwipeea.tuesday.data.api.ApiService;
import com.shakdwipeea.tuesday.data.entities.user.User;
import com.shakdwipeea.tuesday.util.Util;

/**
 * Created by akash on 20/12/16.
 */

public class OtpPresenter implements OtpContract.Presenter {
    private OtpContract.View otpView;
    private ApiService apiService;
    private FirebaseAuth firebaseAuth;

    public OtpPresenter(OtpContract.View otpView) {
        this.otpView = otpView;
        this.apiService = ApiFactory.getInstance();
        this.firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void verifyOtp(String otp, String phoneNumber) {
        User user = new User();
        user.otp = otp;
        user.phoneNumber = phoneNumber;

        apiService.verifyOtp(user)
                .compose(Util.applySchedulers())
                .doOnSubscribe(() -> otpView.displayProgressbar(true))
                .doOnCompleted(() -> otpView.displayProgressbar(false))
                .subscribe(
                        user1 -> {
                            if (TextUtils.isEmpty(user1.name))
                                otpView.launchDetailsInputView(user1.token);
                            else {
                                signInUser(user1);
                            }
                        },
                        throwable -> {
                            otpView.displayError(throwable.getMessage());
                            throwable.printStackTrace();
                        }
                );
    }

    private void signInUser(User user) {
        firebaseAuth.signInWithCustomToken(user.token)
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        otpView.displayError("Could not sign in you in. " +
                                "Now you stay here for eternity");
                    }
                });
    }
}
