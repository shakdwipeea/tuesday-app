package com.shakdwipeea.tuesday.home.home;

import android.content.Context;

import com.shakdwipeea.tuesday.data.entities.user.User;

import java.util.List;

import rx.Observable;

/**
 * Created by ashak on 05-11-2016.
 */

public interface HomeContract {
    interface View {
        void displayError(String message);
        void displayPhoneContacts(List<User> user);
        void addPhoneContact(User user);
        boolean hasPermissions();
        void addTuesContact(User user);
        void clearTuesContact();

        void showTuesidInput(boolean enable);
        void openTuesContact(User user);

        void showProgress(boolean enable);
    }

    interface Presenter {
        void subscribe(Context context);
        void getContacts(Context context);
        void unSubscribe();
        void getTuesContact(String tuesId);
        void searchFriends(String pattern);
    }
}
