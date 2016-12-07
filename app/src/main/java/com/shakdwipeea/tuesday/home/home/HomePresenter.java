package com.shakdwipeea.tuesday.home.home;

import android.content.Context;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.shakdwipeea.tuesday.data.Preferences;
import com.shakdwipeea.tuesday.data.api.ApiFactory;
import com.shakdwipeea.tuesday.data.contacts.ContactsService;
import com.shakdwipeea.tuesday.data.entities.HttpResponse;
import com.shakdwipeea.tuesday.data.entities.User;
import com.shakdwipeea.tuesday.data.firebase.FirebaseService;
import com.shakdwipeea.tuesday.data.firebase.UserService;
import com.shakdwipeea.tuesday.util.Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

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

    // control flow in case subscribe is called twice
    private boolean reqNewTuesId;

    private boolean showTuesId = true;

    HomePresenter(HomeContract.View homeView) {
        this.homeView = homeView;
    }

    //todo not sure if passing the context here is a good idea
    @Override
    public void subscribe(Context context) {
        long t = System.currentTimeMillis();

        Log.d(TAG, "subscribe: Subscribing start " + t);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        Log.d(TAG, "subscribe: Subscribing complete 1 " + (System.currentTimeMillis() - t));
        t = System.currentTimeMillis();
        userService = UserService.getInstance();

        Log.d(TAG, "subscribe: Subscribing complete 2 " + (System.currentTimeMillis() - t));
        t = System.currentTimeMillis();
        preferences = Preferences.getInstance(context);

        Log.d(TAG, "subscribe: Subscribing complete 3 " + (System.currentTimeMillis() - t));
        t = System.currentTimeMillis();

        if (homeView.hasPermissions()) {
            getContacts(context);
        }
        Log.d(TAG, "subscribe: Subscribing complete 4 " + (System.currentTimeMillis() - t));
        t = System.currentTimeMillis();


        // Check if name is already indexed if not then index it
        registerProfile();
        Log.d(TAG, "subscribe: Subscribing complete 5 " + (System.currentTimeMillis() - t));
        t = System.currentTimeMillis();

        //getFriendList();
        Log.d(TAG, "subscribe: Subscribing complete 6 " + (System.currentTimeMillis() - t));
        t = System.currentTimeMillis();

        getTuesID();
        Log.d(TAG, "subscribe: Subscribing complete 7 " + (System.currentTimeMillis() - t));
    }

    private void registerProfile() {
        FirebaseService firebaseService = new FirebaseService(firebaseUser.getUid());
        firebaseService.getProfile()
                .doOnNext(user -> {
                    if (user == null || user.name == null) userService.saveUserDetails();
                    else
                        Log.e(TAG, "registerProfile: OOOH user was not null");
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                        user -> {
                            if ((user == null || user.isIndexed == null || !user.isIndexed)
                                    && (user != null && user.tuesId != null)) {
                                indexName(user.tuesId);
                            }
                        },
                        throwable -> {
                            homeView.displayError(throwable.getMessage());
                        }
                );
    }

    private void getTuesID() {
        homeView.displayTuesIdProgress(true);
        userService.getTuesId()
                .doOnNext(tuesId -> {
                    homeView.displayTuesIdProgress(false);
                    if (tuesId == null) {
                        getNewTuesId();
                     } else {
                        homeView.displayTuesId(tuesId);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(
                        tuesId -> {},
                        throwable -> {
                            homeView.displayTuesIdFailure();
                            homeView.displayTuesIdProgress(false);
                            homeView.displayError(throwable.getMessage());
                            throwable.printStackTrace();
                        }
                );
    }

    private void getNewTuesId() {
        if (!reqNewTuesId) {
            reqNewTuesId = true;
            ApiFactory.getInstance().getTuesID()
                    .map(tuesIDResponse -> tuesIDResponse.tuesID)
                    .doOnNext(tuesId -> userService.setTuesId(tuesId))
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            this::indexName,
                            throwable -> {
                                homeView.displayTuesIdFailure();
                                homeView.displayError(throwable.getMessage());
                            }
                    );
        }
    }

    public void searchFriends(String pattern) {
        userService.getTuesContacts()
                .doOnNext((uidList) -> filterFriends(uidList, pattern))
                .compose(Util.applySchedulers())
                .subscribe();
    }

    private void filterFriends(ArrayList<String> uidList, String pattern) {
        fetchProfile(uidList)
                .filter(user -> user.name.toLowerCase().contains(pattern.toLowerCase()))
                .doOnSubscribe(() -> homeView.clearTuesContact())
                .subscribe(
                        user -> homeView.addTuesContact(user),
                        throwable -> {
                            homeView.displayError(throwable.getMessage());
                            throwable.printStackTrace();
                        }
                );
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
        // TODO: 03-12-2016 add subscription
        userService.getTuesContacts()
                .doOnNext(this::getProfile)
                .compose(Util.applySchedulers())
                .subscribe();
    }

    public void getProfile(ArrayList<String> uidList) {
        fetchProfile(uidList)
                .doOnSubscribe(() -> homeView.clearTuesContact())
                .subscribe(
                        user -> homeView.addTuesContact(user),
                        throwable -> {
                            homeView.displayError(throwable.getMessage());
                            throwable.printStackTrace();
                        }
                );
    }


    public void getContacts(Context context) {
        contactsService = ContactsService.getInstance(context);
        contactsService.getContacts()
                .map(contact -> {
                    Log.d(TAG, "getContacts: " + contact);
                    User user = new User();
                    user.name = contact.name;
                    user.phoneNumber = contact.phone;
                    user.photo = contact.thumbNail;
                    return user;
                })
                .cache()
                .toList()
                .compose(Util.applySchedulers())
                .subscribe(
                        contactList -> {
                            homeView.displayPhoneContacts(contactList);
                        },
                        Throwable::printStackTrace
                );
    }

    @Override
    public Observable<List<User>> searchName(String name) {
        Log.d(TAG, "searchName: Called " + name);

        return ApiFactory.getInstance().searchName(name)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private void indexName(String tuesId) {
        User user = new User();
        user.name = firebaseUser.getDisplayName();
        user.uid = firebaseUser.getUid();
        user.tuesId = tuesId;

        if (firebaseUser.getPhotoUrl() != null)
            user.pic = firebaseUser.getPhotoUrl().toString();

        ApiFactory.getInstance().indexName(user)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        genResponse -> {
                            preferences.setNameIndexed(true);
                            userService.setIndexed(true);
                        },
                        throwable -> {
                            if (throwable instanceof HttpException) {
                                try {
                                    ResponseBody errorBody = ((HttpException) throwable)
                                            .response().errorBody();
                                    HttpResponse.GenResponse response =
                                            new Gson().fromJson(
                                                    errorBody.string(),
                                                    HttpResponse.GenResponse.class
                                            );
                                    homeView.displayError(response.message);

                                } catch (IOException ex){
                                    ex.printStackTrace();
                                }
                            }
                        }
                );
    }

    public void addTuesContact() {
        if (showTuesId) {
            homeView.showTuesidInput(true);
            showTuesId = false;
        }
        else {
            homeView.showTuesidInput(false);
            showTuesId = true;
        }
    }

    @Override
    public void unsubscribe() {

    }

    @Override
    public void getTuesContact(String tuesId) {
        Log.d(TAG, "getTuesContact: Search for " + tuesId);
        ApiFactory.getInstance().getContact(tuesId)
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
    }
}