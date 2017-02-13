package com.shakdwipeea.tuesday.auth.otp;


import android.Manifest;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.shakdwipeea.tuesday.R;
import com.shakdwipeea.tuesday.auth.details.DetailsFragment;
import com.shakdwipeea.tuesday.data.SmsReceiver;
import com.shakdwipeea.tuesday.databinding.FragmentPhoneInputBinding;
import com.shakdwipeea.tuesday.util.Util;
import com.shakdwipeea.tuesday.util.perm.PermViewUtil;
import com.shakdwipeea.tuesday.util.perm.RequestPermissionInterface;

import java.util.concurrent.TimeUnit;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * A simple {@link Fragment} subclass.
 */
public class OtpFragment extends Fragment implements OtpContract.View,
        RequestPermissionInterface {
    private static final String TAG = "OtpFragment";

    FragmentPhoneInputBinding binding;

    private Subscription subscription;

    private MaterialDialog dialog;

    private OtpPresenter presenter;

    PermViewUtil permViewUtil;

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
        binding.phoneInput.setHint(R.string.otp_hint);

        permViewUtil = new PermViewUtil(binding.getRoot());
        permViewUtil.performActionWithPermissions(
                getContext(),
                Manifest.permission.RECEIVE_SMS,
                new String[]{Manifest.permission.RECEIVE_SMS},
                this,
                this::setupSmsReceiver
        );
    }

    /**
     * Callback for the result from requesting permissions. This method
     * is invoked for every call on {@link #requestPermissions(String[], int)}.
     * <p>
     * <strong>Note:</strong> It is possible that the permissions request interaction
     * with the user is interrupted. In this case you will receive empty permissions
     * and results arrays which should be treated as a cancellation.
     * </p>
     *
     * @param requestCode  The request code passed in {@link #requestPermissions(String[], int)}.
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either {@link PackageManager#PERMISSION_GRANTED}
     *                     or {@link PackageManager#PERMISSION_DENIED}. Never null.
     * @see #requestPermissions(String[], int)
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permViewUtil.onPermissionResult(requestCode, permissions, grantResults);
    }

    private void setupSmsReceiver() {
        SmsReceiver.bindListener(message -> {
            binding.phoneInput.setText(message.split(" ")[0]);
        });
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
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT)
                .show();
    }
}
