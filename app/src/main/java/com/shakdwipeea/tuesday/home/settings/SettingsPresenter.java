package com.shakdwipeea.tuesday.home.settings;

import android.content.Context;
import android.view.View;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.shakdwipeea.tuesday.data.Preferences;
import com.shakdwipeea.tuesday.data.firebase.UserService;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by ashak on 27-11-2016.
 */

public class SettingsPresenter implements SettingsContract.Presenter {
    private SettingsContract.View settingsView;
    private UserService userService;
    private Preferences preferences;

    public SettingsPresenter(SettingsContract.View settingsView, Context context) {
        this.settingsView = settingsView;
        userService = UserService.getInstance();
        preferences = Preferences.getInstance(context);
    }

    @Override
    public void getUser() {
        userService.getUserDetails()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(() -> settingsView.showProgress(true))
                .doOnCompleted(() -> settingsView.showProgress(false))
                .doOnError(throwable -> settingsView.showProgress(false))
                .subscribe(
                        user -> settingsView.displayUserDetails(user),
                        throwable -> {
                            settingsView.displayError(throwable.getMessage());
                            throwable.printStackTrace();
                        }
                );
    }

    public void logOut(View view) {
        FirebaseAuth.getInstance().signOut();

        // explicit sign out from facebook
        //if (provider.equals(AuthService.FACEBOOK_AUTH_PROVIDER))
        LoginManager.getInstance().logOut();
        preferences.clear();

        settingsView.launchAuth();
    }
}