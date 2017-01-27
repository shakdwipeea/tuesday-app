package com.shakdwipeea.tuesday.profile.edit;

import com.shakdwipeea.tuesday.data.entities.user.Provider;
import com.shakdwipeea.tuesday.data.entities.user.User;
import com.shakdwipeea.tuesday.data.firebase.UserService;
import com.shakdwipeea.tuesday.data.providers.ProviderNames;
import com.shakdwipeea.tuesday.util.Util;

import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by akash on 24/1/17.
 */

public class EditProfilePresenter implements EditProfileContract.Presenter {
    private EditProfileContract.View editProfileView;
    private UserService userService;
    private CompositeSubscription compositeSubscription;

    public EditProfilePresenter(EditProfileContract.View editProfileView) {
        this.editProfileView = editProfileView;
        this.userService = UserService.getInstance();
        this.compositeSubscription = new CompositeSubscription();
    }

    @Override
    public void loadProviders() {
        editProfileView.displayProgress(true);
        Subscription subscription = userService.getProvider()
                .compose(Util.applySchedulers())
                .doOnNext(this::processProviders)
                .subscribe(
                        providerList -> editProfileView.displayProgress(false),
                        Throwable::printStackTrace
                );
        compositeSubscription.add(subscription);
    }

    private void processProviders(List<Provider> providerList) {
        editProfileView.clearMailDetails();
        editProfileView.clearCallDetails();
        editProfileView.clearProvider();

        Subscription subscription = Observable.from(providerList)
                .filter(provider -> {
                    switch (provider.name) {
                        case ProviderNames.Call:
                            editProfileView.addCallDetails(provider.providerDetails);
                            return false;

                        case ProviderNames.Email:
                            editProfileView.addMailDetails(provider.providerDetails);
                            return false;

                        default:
                            editProfileView.addProvider(provider.providerDetails);
                            return true;
                    }
                })
                .subscribe(
                        provider -> {
                        },
                        Throwable::printStackTrace
                );
        compositeSubscription.add(subscription);
    }

    @Override
    public void subscribe() {
        loadProviders();
    }

    @Override
    public void unSubscribe() {
        compositeSubscription.unsubscribe();
    }
}
