package com.shakdwipeea.tuesday.auth.otp;


import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.auth.FirebaseUser;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.shakdwipeea.tuesday.R;
import com.shakdwipeea.tuesday.auth.details.DetailsFragment;
import com.shakdwipeea.tuesday.data.Preferences;
import com.shakdwipeea.tuesday.databinding.FragmentPhoneInputBinding;
import com.shakdwipeea.tuesday.home.HomeActivity;
import com.shakdwipeea.tuesday.profile.ProfileActivity;
import com.shakdwipeea.tuesday.setup.picker.ProviderPickerActivity;
import com.shakdwipeea.tuesday.util.Util;

import java.util.concurrent.TimeUnit;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * A simple {@link Fragment} subclass.
 */
public class OtpFragment extends Fragment implements OtpContract.View {
    FragmentPhoneInputBinding binding;

    private Subscription subscription;

    private MaterialDialog dialog;

    private OtpPresenter presenter;

    String phone;

    public static final String PHONE_NUMBER_ARG_KEY = "phone";

    public OtpFragment() {
        // Required empty public constructor
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        subscription.unsubscribe();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_phone_input,
                container, false);

        phone = getArguments().getString(PHONE_NUMBER_ARG_KEY);

        if (TextUtils.isEmpty(phone))
            displayError("Incorrect usage");

        presenter = new OtpPresenter(this);

        setupForOtp();

        subscription = RxTextView.textChanges(binding.phoneInput)
                .debounce(200, TimeUnit.MILLISECONDS)
                .filter(charSequence -> charSequence.length() == 6)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(charSequence -> presenter.verifyOtp(charSequence.toString(), phone))
                .subscribe();

        return binding.getRoot();
    }

    private void setupForOtp() {
        binding.phoneInputLabel.setText(R.string.otp_label);

        binding.phoneInput.setInputType(EditorInfo.TYPE_CLASS_TEXT);
        binding.phoneInput.setHint(R.string.otp_hint);
    }

    @Override
    public void displayProgressbar(boolean enable) {
        if (dialog == null)
            dialog = Util.createProgressDialog(getContext()).show();

        if (enable)
            dialog.show();
        else
            dialog.dismiss();
    }

    @Override
    public void launchDetailsInputView(String token) {
        DetailsFragment detailsFragment = new DetailsFragment();

        Bundle args = new Bundle();
        args.putString(DetailsFragment.KEY_TOKEN, token);
        args.putString(DetailsFragment.KEY_PHONE, phone);
        detailsFragment.setArguments(args);

        FragmentTransaction fragmentTransaction = getActivity()
                .getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.auth_fragment_container, detailsFragment);
        fragmentTransaction.commit();

    }

    @Override
    public void displayError(String message) {
        Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_SHORT)
                .show();
    }
}
