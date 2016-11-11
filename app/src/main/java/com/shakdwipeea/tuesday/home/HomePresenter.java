package com.shakdwipeea.tuesday.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.shakdwipeea.tuesday.data.ContactsProvider;
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
    private SharedPreferences preferences;
    private ContactsProvider contactsProvider;

    private HomeContract.View homeView;

    HomePresenter(HomeContract.View homeView) {
        this.homeView = homeView;
    }

    // not sure if passing the context here is a good idea
    @Override
    public void subscribe(Context context) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userService = UserService.getInstance();
        contactsProvider = new ContactsProvider(context);

        preferences = context
                .getSharedPreferences(Preferences.SHARED_PREFS_FILE_NAME, Context.MODE_PRIVATE);

        if (!Preferences.isNameIndexed(preferences)) {
            // Save user details in the firebase
            userService.saveUserDetails();
            indexName();
        }

        getContacts();
    }

    public void getContacts() {
        contactsProvider.getContacts()
                .flatMap(Observable::from)
                .map(contact -> {
                    Log.d(TAG, "getContacts: " + contact);
                    User user = new User();
                    user.name = contact.name;
                    user.phoneNumber = contact.phone;
                    user.photo = contact.thumbNail;
                    return user;
                })
                .toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(
                        contacts -> homeView.displayPhoneContacts(contacts),
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
                        genResponse -> Preferences.setNameIndexed(preferences, true),
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