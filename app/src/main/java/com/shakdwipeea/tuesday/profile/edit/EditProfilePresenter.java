package com.shakdwipeea.tuesday.profile.edit;

import android.text.TextUtils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.shakdwipeea.tuesday.data.entities.user.Provider;
import com.shakdwipeea.tuesday.data.entities.user.User;
import com.shakdwipeea.tuesday.data.firebase.UserService;
import com.shakdwipeea.tuesday.util.Util;

import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by akash on 24/1/17.
 */

public class EditProfilePresenter implements EditProfileContract.Presenter,
        EditProfileContract.ItemPresenter {
    private EditProfileContract.View editProfileView;
    private UserService userService;
    private CompositeSubscription compositeSubscription;

    private FirebaseUser user;

    public EditProfilePresenter(EditProfileContract.View editProfileView) {
        this.editProfileView = editProfileView;
        this.userService = UserService.getInstance();
        this.compositeSubscription = new CompositeSubscription();
        this.user = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public void loadProviders() {
//        editProfileView.setProgressBar(true);

        // We dont want to watch changes here as all the update is done here itself
        Subscription subscription = userService.getProvider()
                .compose(Util.applySchedulers())
                .first()
                .doOnNext(this::processProviders)
                .subscribe(
                        providerList -> editProfileView.setProgressBar(false),
                        Throwable::printStackTrace
                );
        compositeSubscription.add(subscription);
    }

    private void processProviders(List<Provider> providerList) {
        editProfileView.clearProvider();

        Subscription subscription = Observable.from(providerList)
                .doOnNext(provider -> editProfileView.addProvider(provider))
                .subscribe(
                        provider -> {},
                        Throwable::printStackTrace
                );
        compositeSubscription.add(subscription);
    }

    @Override
    public void changeName(String name) {
        UserProfileChangeRequest changeRequest = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null)
            currentUser.updateProfile(changeRequest);
        else
            editProfileView.displayError("You are not signed in");
        userService.setName(name);
    }

    @Override
    public void subscribe() {
        loadProviders();

        User user = new User();
        user.name = this.user.getDisplayName();
        user.uid = this.user.getUid();
        user.pic = this.user.getPhotoUrl().toString();
        editProfileView.displayUser(user);
    }

    @Override
    public void unSubscribe() {
        compositeSubscription.unsubscribe();
    }

    @Override
    public void saveDetails(Provider provider) {
        if (TextUtils.isEmpty(provider.providerDetails.getUsername()) &&
                TextUtils.isEmpty(provider.providerDetails.getPhoneNumber())) {
            return;
        }

        userService.saveProvider(provider);
    }

    @Override
    public void deleteDetail(Provider provider) {
        userService.deleteProvider(provider);
        loadProviders();
    }
}
