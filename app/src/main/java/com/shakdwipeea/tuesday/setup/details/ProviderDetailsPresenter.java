package com.shakdwipeea.tuesday.setup.details;

import android.util.Log;

import com.shakdwipeea.tuesday.data.entities.Provider;
import com.shakdwipeea.tuesday.data.firebase.UserService;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.twitter.sdk.android.core.TwitterCore.TAG;

/**
 * Created by ashak on 11-11-2016.
 */

public class ProviderDetailsPresenter implements ProviderDetailsContract.Presenter {
    private ProviderDetailsContract.View providerDetailsView;
    private UserService userService;

    private Provider provider;

    public ProviderDetailsPresenter(ProviderDetailsContract.View providerDetailsView
            , Provider provider) {
        this.providerDetailsView = providerDetailsView;
        this.provider = provider;
        this.userService = UserService.getInstance();
    }

    @Override
    public void saveProviderDetails() {
        userService.saveProvider(provider)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                        aVoid -> {
                            Log.d(TAG, "saveProviderDetails: Profile saved successfully");
                        },
                        throwable -> {
                            providerDetailsView.displayError(throwable.getMessage());
                            throwable.printStackTrace();
                        }
                );
    }
}
