package com.shakdwipeea.tuesday.people;

import android.util.Log;

import com.shakdwipeea.tuesday.data.firebase.FirebaseService;
import com.shakdwipeea.tuesday.data.firebase.UserService;
import com.shakdwipeea.tuesday.util.Util;

import java.util.ArrayList;

import rx.Observable;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by ashak on 03-12-2016.
 */

public class PeopleListPresenter implements PeopleListContract.Presenter {
    private static final String TAG = "PeopleListPresenter";

    private PeopleListContract.View peopleView;
    private UserService userService;

    private CompositeSubscription subscription;

    public PeopleListPresenter(PeopleListContract.View peopleView) {
        this.peopleView = peopleView;
        userService = UserService.getInstance();
    }

    @Override
    public void subscribe() {
        subscription = new CompositeSubscription();
        subscription.add(getPeople());
    }

    @Override
    public void unsubscribe() {
        subscription.unsubscribe();
    }

    @Override
    public Subscription getPeople() {
        return userService.getAddedBy()
                .compose(Util.applySchedulers())
                .doOnNext(this::getProfile)
                .subscribe(
                        strings -> Log.d(TAG, "getPeople: Got people"),
                        throwable -> {
                            peopleView.displayError(throwable.getMessage());
                            throwable.printStackTrace();
                        }
                );
    }

    public void getProfile(ArrayList<String> uidList) {
        Subscription profileSubscription = Observable.from(uidList)
                .flatMap(uid -> {
                    FirebaseService firebaseService = new FirebaseService(uid);
                    return firebaseService.getProfile();
                })
                .compose(Util.applySchedulers())
                .doOnSubscribe(() -> peopleView.clearPeople())
                .doOnNext(user -> peopleView.addPerson(user))
                .subscribe(
                        user -> {
                        },
                        throwable -> {
                            peopleView.displayError(throwable.getMessage());
                            throwable.printStackTrace();
                        }
                );

        subscription.add(profileSubscription);
    }
}
