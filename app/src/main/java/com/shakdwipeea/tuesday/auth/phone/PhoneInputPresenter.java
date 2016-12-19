package com.shakdwipeea.tuesday.auth.phone;

/**
 * Created by akash on 19/12/16.
 */

public class PhoneInputPresenter implements PhoneInputContract.Presenter {
    private PhoneInputContract.View phoneInputView;

    public PhoneInputPresenter(PhoneInputContract.View phoneInputView) {
        this.phoneInputView = phoneInputView;
    }

    @Override
    public void getAccountDetails(String phoneNumber) {

    }
}
