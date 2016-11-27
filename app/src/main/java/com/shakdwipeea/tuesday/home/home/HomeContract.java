package com.shakdwipeea.tuesday.home.home;

import android.content.Context;

import com.shakdwipeea.tuesday.data.entities.User;

import java.util.List;

import rx.Observable;

/**
 * Created by ashak on 05-11-2016.
 */

public interface HomeContract {
    interface View {
        void displayError(String message);
        void displayPhoneContacts(List<User> user);
        void displayTuesId(String tuesId);
        void addPhoneContact(User user);
        void displayTuesIdProgress(Boolean value);
        void displayTuesIdFailure();
        boolean hasPermissions();
        void addTuesContact(User user);
    }

    interface Presenter {
        void subscribe(Context context);
        Observable<List<User>> searchName(String name);
        void getContacts();
        void unsubscribe();
    }
}