package com.shakdwipeea.tuesday.auth.phone;


import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.shakdwipeea.tuesday.R;
import com.shakdwipeea.tuesday.auth.otp.OtpFragment;
import com.shakdwipeea.tuesday.databinding.FragmentPhoneInputBinding;
import com.shakdwipeea.tuesday.home.HomeActivity;
import com.shakdwipeea.tuesday.util.Util;

import java.util.concurrent.TimeUnit;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;
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
    public void launchOtpView() {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager()
                .beginTransaction();
        fragmentTransaction.replace(R.id.auth_fragment_container, new OtpFragment());
        fragmentTransaction.commit();
    }

    @Override
    public void displayProgressBar(boolean enable) {
        if (progressDialog == null)
            progressDialog = new MaterialDialog.Builder(getContext())
                    .title(R.string.progress_dialog)
                    .content(R.string.please_wait)
                    .progress(true, 0).show();

        if (enable) progressDialog.show();
        else progressDialog.dismiss();
    }
}
