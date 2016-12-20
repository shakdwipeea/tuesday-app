package com.shakdwipeea.tuesday.auth.phone;

import com.shakdwipeea.tuesday.data.api.ApiFactory;
import com.shakdwipeea.tuesday.data.api.ApiService;
import com.shakdwipeea.tuesday.data.entities.user.User;
import com.shakdwipeea.tuesday.util.Util;

/**
 * Created by akash on 19/12/16.
 */

public class PhoneInputPresenter implements PhoneInputContract.Presenter {
    private PhoneInputContract.View phoneInputView;

    private ApiService apiService;

    public PhoneInputPresenter(PhoneInputContract.View phoneInputView) {
        this.phoneInputView = phoneInputView;
        apiService = ApiFactory.getInstance();
    }

    @Override
    public void getAccountDetails(String phoneNumber) {
        phoneInputView.displayProgressBar(true);

        User user = new User();
        user.phoneNumber = phoneNumber;

        apiService.getAccountDetails(user)
                .compose(Util.applySchedulers())
                .subscribe(
                        user1 -> {
                            phoneInputView.displayProgressBar(false);
                            phoneInputView.launchOtpView(user1.phoneNumber);
                        },
                        throwable -> {
                            throwable.printStackTrace();
                            phoneInputView.displayProgressBar(false);
                            phoneInputView.displayError(throwable.getMessage());
                        }
                );
    }
}
