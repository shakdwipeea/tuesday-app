package com.shakdwipeea.tuesday.auth.otp;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.shakdwipeea.tuesday.data.api.ApiFactory;
import com.shakdwipeea.tuesday.data.api.ApiService;
import com.shakdwipeea.tuesday.data.entities.user.User;
import com.shakdwipeea.tuesday.util.Util;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by akash on 20/12/16.
 */

public class OtpPresenter implements OtpContract.Presenter {
    private static final String TAG = "OtpPresenter";

    private OtpContract.View otpView;
    private ApiService apiService;
    private FirebaseAuth firebaseAuth;

    private String signInToken;

    private FirebaseAuth.AuthStateListener authListener;
    private CompositeSubscription compositeSubscription;

    public OtpPresenter(OtpContract.View otpView) {
        this.otpView = otpView;
        this.apiService = ApiFactory.getInstance();
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.compositeSubscription = new CompositeSubscription();
        setupListener();
    }

    public void setupListener() {
        authListener = auth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            Log.d(TAG, "init: User is " + user);

            if (user != null) {
                otpView.displayProgressbar(false);

                // User is signed in
                Log.e(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                otpView.launchDetailsInputView(signInToken);

                // when profile page is open we don't need this
                auth.removeAuthStateListener(authListener);
            } else {
                // User is signed out
                Log.d(TAG, "onAuthStateChanged:signed_out");
            }

        };

        firebaseAuth.addAuthStateListener(authListener);
    }

    @Override
    public void verifyOtp(String otp, String phoneNumber) {
        User user = new User();
        user.otp = otp;
        user.phoneNumber = phoneNumber;

        Subscription subscribe = apiService.verifyOtp(user)
                .compose(Util.applySchedulers())
                .doOnSubscribe(() -> otpView.displayProgressbar(true))
                .subscribe(
                        this::signInUser,
                        throwable -> {
                            otpView.displayProgressbar(false);
                            otpView.displayError(throwable.getMessage());
                            throwable.printStackTrace();
                        }
                );
        compositeSubscription.add(subscribe);
    }

    private void signInUser(User user) {
        signInToken = user.token;

        firebaseAuth.signInWithCustomToken(user.token)
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        otpView.displayError("Could not sign in you in. " +
                                "Now you stay here for eternity");
                    }
                });
    }

    public void unsubscribe() {
        compositeSubscription.clear();
    }
}
