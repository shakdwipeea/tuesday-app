package com.shakdwipeea.tuesday.people;

import com.shakdwipeea.tuesday.data.entities.user.User;

import rx.Subscription;

/**
 * Created by ashak on 03-12-2016.
 */

public class PeopleListContract {
    interface View {
        void displayError(String reason);
        void displayProgressBar(boolean enable);
        void addPerson(User person);
        void clearPeople();
    }

    interface Presenter {
        void subscribe();
        void unsubscribe();
        Subscription getPeople();
    }
}
