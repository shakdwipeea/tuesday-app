package com.shakdwipeea.tuesday.auth.phone;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jakewharton.rxbinding.widget.RxTextView;
import com.shakdwipeea.tuesday.R;
import com.shakdwipeea.tuesday.databinding.FragmentPhoneInputBinding;

import java.util.concurrent.TimeUnit;

import rx.Subscription;

/**
 * A simple {@link Fragment} subclass.
 */
public class PhoneInputFragment extends Fragment implements PhoneInputContract.View {
    FragmentPhoneInputBinding binding;

    Subscription viewSubscription;

    PhoneInputContract.Presenter presenter;

    public PhoneInputFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil
                .inflate(inflater, R.layout.fragment_phone_input, container, false);

        presenter = new PhoneInputPresenter(this);

        RxTextView.textChanges(binding.phoneInput)
                .debounce(200, TimeUnit.MILLISECONDS)
                .filter(charSequence -> charSequence.length() == 10)
                .doOnNext(charSequence -> presenter.getAccountDetails(charSequence.toString()))
                .subscribe();

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewSubscription.unsubscribe();
    }

    @Override
    public void displayError(String message) {

    }

    @Override
    public void launchOtpView() {

    }

    @Override
    public void launchHomeView() {

    }
}
