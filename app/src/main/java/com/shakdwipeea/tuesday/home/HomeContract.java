package com.shakdwipeea.tuesday.home;

import android.content.Context;

import com.shakdwipeea.tuesday.api.entities.User;

import java.util.List;

import rx.Observable;

/**
 * Created by ashak on 05-11-2016.
 */

public interface HomeContract {
    interface View {
        void displayError(String message);
    }

    interface Presenter {
        void subscribe(Context context);
        Observable<List<User>> searchName(String name);
        void unsubscribe();
    }
}
