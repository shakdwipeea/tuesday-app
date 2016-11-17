package com.shakdwipeea.tuesday.home;

import android.content.Context;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.shakdwipeea.tuesday.data.ContactsService;
import com.shakdwipeea.tuesday.data.Preferences;
import com.shakdwipeea.tuesday.data.api.ApiFactory;
import com.shakdwipeea.tuesday.data.entities.HttpResponse;
import com.shakdwipeea.tuesday.data.entities.User;
import com.shakdwipeea.tuesday.data.firebase.UserService;

import java.io.IOException;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.twitter.sdk.android.core.TwitterCore.TAG;

/**
 * Created by ashak on 05-11-2016.
 */

public class HomePresenter implements HomeContract.Presenter {
    private FirebaseUser firebaseUser;
    private UserService userService;
    private ContactsService contactsService;

    private HomeContract.View homeView;

    private Preferences preferences;

    // control flow in case subscribe is called twice
    private boolean reqNewTuesId;

    HomePresenter(HomeContract.View homeView) {
        this.homeView = homeView;
    }

    //todo not sure if passing the context here is a good idea
    @Override
    public void subscribe(Context context) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userService = UserService.getInstance();
        contactsService = new ContactsService(context);
        preferences = Preferences.getInstance(context);

        if (!preferences.isNameIndexed()) {
            // Save user details in the firebase
            userService.saveUserDetails();
            indexName();
        }

        getTuesID();
        getContacts();
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
                            s -> {},
                            throwable -> {
                                homeView.displayTuesIdFailure();
                                homeView.displayError(throwable.getMessage());
                            }
                    );
        }
    }


    public void getContacts() {
        contactsService.getContacts()
                .map(contact -> {
                    Log.d(TAG, "getContacts: " + contact);
                    User user = new User();
                    user.name = contact.name;
                    user.phoneNumber = contact.phone;
                    user.photo = contact.thumbNail;
                    return user;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(
                        contact -> homeView.addPhoneContact(contact),
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

    private void indexName() {
        User user = new User();
        user.name = firebaseUser.getDisplayName();
        user.uid = firebaseUser.getUid();

        if (firebaseUser.getPhotoUrl() != null)
            user.pic = firebaseUser.getPhotoUrl().toString();

        ApiFactory.getInstance().indexName(user)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        genResponse -> preferences.setNameIndexed(true),
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

    @Override
    public void unsubscribe() {

    }
}