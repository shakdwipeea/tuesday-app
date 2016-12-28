package com.shakdwipeea.tuesday.home.home;

import android.content.Context;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.shakdwipeea.tuesday.data.Preferences;
import com.shakdwipeea.tuesday.data.api.ApiFactory;
import com.shakdwipeea.tuesday.data.contacts.ContactsService;
import com.shakdwipeea.tuesday.data.entities.user.User;
import com.shakdwipeea.tuesday.data.firebase.FirebaseService;
import com.shakdwipeea.tuesday.data.firebase.UserService;
import com.shakdwipeea.tuesday.home.OnBackPressedListener;
import com.shakdwipeea.tuesday.util.Util;

import java.util.ArrayList;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by ashak on 05-11-2016.
 */

public class HomePresenter implements HomeContract.Presenter {
    private static final String TAG = "HomePresenter";

    private FirebaseUser firebaseUser;
    private UserService userService;
    private ContactsService contactsService;

    private HomeContract.View homeView;

    private Preferences preferences;

    private boolean showTuesId = true;

    private CompositeSubscription compositeSubscription;

    HomePresenter(HomeContract.View homeView) {
        this.homeView = homeView;
        compositeSubscription = new CompositeSubscription();
    }

    //todo not sure if passing the context here is a good idea
    @Override
    public void subscribe(Context context) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        userService = UserService.getInstance();
        preferences = Preferences.getInstance(context);

        if (homeView.hasPermissions()) {
            getContacts(context);
        }

        // Check if name is already indexed if not then index it
        registerProfile();
    }

    @Override
    public void unSubscribe() {
        compositeSubscription.unsubscribe();
    }

    private void registerProfile() {
        FirebaseService firebaseService = new FirebaseService(firebaseUser.getUid());
        Subscription subscription = firebaseService.getProfile()
                .doOnNext(user -> {
                    if (user == null || user.name == null) userService.saveUserDetails();
                    else
                        Log.e(TAG, "registerProfile: OOOH user was not null");
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                        user -> {},
                        throwable -> {
                            homeView.displayError(throwable.getMessage());
                        }
                );
        compositeSubscription.add(subscription);
    }

    public void searchFriends(String pattern) {
        Subscription subscription = userService.getTuesContacts()
                .doOnNext((uidList) -> filterFriends(uidList, pattern))
                .compose(Util.applySchedulers())
                .subscribe(
                        strings -> {},
                        Throwable::printStackTrace
                );
        compositeSubscription.add(subscription);
    }

    private void filterFriends(ArrayList<String> uidList, String pattern) {
        Subscription subscription = fetchProfile(uidList)
                .filter(user -> user.name.toLowerCase().contains(pattern.toLowerCase()))
                .doOnSubscribe(() -> homeView.clearTuesContact())
                .subscribe(
                        user -> homeView.addTuesContact(user),
                        throwable -> {
                            homeView.displayError(throwable.getMessage());
                            throwable.printStackTrace();
                        }
                );
        compositeSubscription.add(subscription);
    }

    private Observable<User> fetchProfile(ArrayList<String> uidList) {
        return Observable.from(uidList)
                .flatMap(s -> {
                    FirebaseService firebaseService = new FirebaseService(s);
                    return firebaseService.getProfile();
                })
                .compose(Util.applySchedulers());
    }

    public void getFriendList() {
        Subscription subscription = userService.getTuesContacts()
                .doOnNext(this::getProfile)
                .compose(Util.applySchedulers())
                .subscribe(
                        strings -> {},
                        Throwable::printStackTrace
                );
        compositeSubscription.add(subscription);
    }

    public void getProfile(ArrayList<String> uidList) {
        Subscription subscription = fetchProfile(uidList)
                .doOnSubscribe(() -> homeView.clearTuesContact())
                .subscribe(
                        user -> homeView.addTuesContact(user),
                        throwable -> {
                            homeView.displayError(throwable.getMessage());
                            throwable.printStackTrace();
                        }
                );
        compositeSubscription.add(subscription);
    }


    public void getContacts(Context context) {
        contactsService = ContactsService.getInstance(context);
        Subscription subscription = contactsService.getContacts()
                .map(contact -> {
                    //Log.d(TAG, "getContacts: " + contact);
                    User user = new User();
                    user.name = contact.name;
                    user.phoneNumber = contact.phone;
                    user.photo = contact.thumbNail;
                    return user;
                })
                .toList()
                .cache()
                .compose(Util.applySchedulers())
                .subscribe(
                        contactList -> {
                            homeView.displayPhoneContacts(contactList);
                        },
                        Throwable::printStackTrace
                );
        compositeSubscription.add(subscription);
    }

    public void toggleTuesContactView() {
        if (showTuesId) {
            homeView.showTuesidInput(true);
            showTuesId = false;
        }
        else {
            homeView.showTuesidInput(false);
            showTuesId = true;
        }
    }

    public OnBackPressedListener getBackPressedListener() {
        return () -> {
            if (!showTuesId) {
                toggleTuesContactView();
                return false;
            }

            return true;
        };
    }

    @Override
    public void getTuesContact(String phoneNumber) {
        Log.d(TAG, "getTuesContact: Search for " + phoneNumber);
        Subscription subscription = ApiFactory.getInstance().getContact(phoneNumber)
                .compose(Util.applySchedulers())
                .doOnSubscribe(() -> homeView.showProgress(true))
                .subscribe(
                        user -> {
                            homeView.showProgress(false);
                            homeView.openTuesContact(user);
                        },
                        throwable -> {
                            homeView.showProgress(false);
                            homeView.displayError("Account not found");
                            throwable.printStackTrace();
                        }
                );
        compositeSubscription.add(subscription);
    }
}