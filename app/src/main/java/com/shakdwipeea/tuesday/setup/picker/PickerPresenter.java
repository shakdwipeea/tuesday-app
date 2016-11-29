package com.shakdwipeea.tuesday.setup.picker;

import com.shakdwipeea.tuesday.data.entities.Provider;
import com.shakdwipeea.tuesday.data.firebase.UserService;
import com.shakdwipeea.tuesday.data.providers.SelectedProviders;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by ashak on 11-11-2016.
 */

public class PickerPresenter implements PickerContract.Presenter {
    private static final String TAG = "PickerPresenter";

    private PickerContract.View pickerView;
    private Subscription subscription;

    public PickerPresenter(PickerContract.View pickerView) {
        this.pickerView = pickerView;
    }

    @Override
    public void subscribe() {
        getProviderInfo();
    }

    private void getProviderInfo() {
        List<Provider> selectedProviders = new ArrayList<>();

//        subscription = UserService.getInstance()
//                .getProvider()
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(
//                        selectedProviders::add,
//                        throwable -> {
//                            pickerView.displayError(throwable.getMessage());
//                            throwable.printStackTrace();
//                        },
//                        () -> SelectedProviders.setProviderList(selectedProviders)
//                );

        Observable<List<Provider>> providerObservable = UserService.getInstance()
                .getProvider()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        SelectedProviders.getInstance()
                .setProviderList(providerObservable);
    }

    @Override
    public void unsubscribe() {
//        subscription.unsubscribe();
    }
}
