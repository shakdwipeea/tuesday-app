package com.shakdwipeea.tuesday.auth.phone;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import com.afollestad.materialdialogs.MaterialDialog;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.shakdwipeea.tuesday.R;
import com.shakdwipeea.tuesday.auth.otp.OtpFragment;
import com.shakdwipeea.tuesday.databinding.FragmentPhoneInputBinding;
import com.shakdwipeea.tuesday.util.Util;

import java.util.concurrent.TimeUnit;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * A simple {@link Fragment} subclass.
 */
public class PhoneInputFragment extends Fragment implements PhoneInputContract.View {
    FragmentPhoneInputBinding binding;

    Subscription viewSubscription;
    MaterialDialog progressDialog;

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

        binding.phoneInput.setOnEditorActionListener(
                (v, actionId, event) -> {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        String phone = binding.phoneInput.getText().toString();
                        if (phone.length() == 10) {
                            presenter.getAccountDetails(phone);
                            return true;
                        }
                    }

                    return false;
                }
        );

        viewSubscription = RxTextView.textChanges(binding.phoneInput)
                .debounce(200, TimeUnit.MILLISECONDS)
                .filter(charSequence -> charSequence.length() == 10)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(charSequence -> presenter.getAccountDetails(charSequence.toString()))
                .subscribe(
                        charSequence -> {},
                        Throwable::printStackTrace
                );

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewSubscription.unsubscribe();
    }

    @Override
    public void displayError(String message) {
        Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_SHORT)
                .show();
    }

    @Override
    public void launchOtpView(String phoneNumber) {
        OtpFragment otpFragment = new OtpFragment();

        Bundle args = new Bundle();
        args.putString(OtpFragment.PHONE_NUMBER_ARG_KEY, phoneNumber);
        otpFragment.setArguments(args);

        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager()
                .beginTransaction();
        fragmentTransaction.replace(R.id.auth_fragment_container, otpFragment);
        fragmentTransaction.commit();
    }

    @Override
    public void displayProgressBar(boolean enable) {
        if (progressDialog == null)
            progressDialog = Util.createProgressDialog(getContext()).show();

        if (enable) progressDialog.show();
        else progressDialog.dismiss();
    }
}
