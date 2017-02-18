package com.shakdwipeea.tuesday.auth.phone;

import android.util.Log;

import com.shakdwipeea.tuesday.data.api.ApiFactory;
import com.shakdwipeea.tuesday.data.api.ApiService;
import com.shakdwipeea.tuesday.data.entities.user.User;
import com.shakdwipeea.tuesday.util.Util;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

import static io.fabric.sdk.android.Fabric.TAG;

/**
 * Created by akash on 19/12/16.
 */

public class PhoneInputPresenter implements PhoneInputContract.Presenter {
    private PhoneInputContract.View phoneInputView;

    private ApiService apiService;
    private CompositeSubscription compositeSubscription;

    public PhoneInputPresenter(PhoneInputContract.View phoneInputView) {
        this.phoneInputView = phoneInputView;
        apiService = ApiFactory.getInstance();
        this.compositeSubscription = new CompositeSubscription();
    }

    @Override
    public void getAccountDetails(String phoneNumber) {
        phoneInputView.displayProgressBar(true);

        User user = new User();
        user.phoneNumber = phoneNumber;

        Subscription subscribe = apiService.getAccountDetails(user)
                .compose(Util.applySchedulers())
                .subscribe(
                        user1 -> {
                            phoneInputView.displayProgressBar(false);
                            Log.d(TAG, "getAccountDetails: From server user1 is " + user1);
                            phoneInputView.launchOtpView(phoneNumber);
                        },
                        throwable -> {
                            throwable.printStackTrace();
                            phoneInputView.displayProgressBar(false);
                            phoneInputView.displayError(throwable.getMessage());
                        }
                );
        compositeSubscription.add(subscribe);
    }
}
