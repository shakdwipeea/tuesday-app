package com.shakdwipeea.tuesday.auth.details;


import android.Manifest;
import android.accounts.Account;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.auth.FirebaseUser;
import com.shakdwipeea.tuesday.R;
import com.shakdwipeea.tuesday.data.Preferences;
import com.shakdwipeea.tuesday.data.contacts.sync.SyncUtils;
import com.shakdwipeea.tuesday.data.entities.user.User;
import com.shakdwipeea.tuesday.databinding.FragmentDetailsBinding;
import com.shakdwipeea.tuesday.home.HomeActivity;
import com.shakdwipeea.tuesday.picture.ProfilePictureUtil;
import com.shakdwipeea.tuesday.util.Util;
import com.shakdwipeea.tuesday.util.perm.PermViewUtil;
import com.shakdwipeea.tuesday.util.perm.RequestPermissionInterface;
import com.squareup.picasso.Picasso;

import rx.Observable;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailsFragment extends Fragment
        implements DetailsContract.View, RequestPermissionInterface {
    public static final String KEY_TOKEN = "token";
    public static final String KEY_PHONE = "phone";
    private static final String TAG = "DetailsFragment";
    FragmentDetailsBinding binding;

    DetailsPresenter presenter;

    MaterialDialog dialog;

    ProfilePictureUtil pictureUtil;
    PermViewUtil permViewUtil;

    private String profileUrl;

    public DetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_details,
                container, false);

        presenter = new DetailsPresenter(this);

        pictureUtil = new ProfilePictureUtil(presenter);

        String token = getArguments().getString(KEY_TOKEN);
        String phone = getArguments().getString(KEY_PHONE);

        binding.saveButton.setOnClickListener(v -> {
            String name = binding.nameInput.getText().toString();
            if (TextUtils.isEmpty(name)) {
                displayError("Please enter your name");
                return;
            }

            User user = new User();
            user.name = name;
            user.token = token;
            user.pic = profileUrl;
            user.phoneNumber = phone;
            presenter.saveDetails(user);
        });

        binding.cameraIcon.setOnClickListener(v -> {
            permViewUtil = new PermViewUtil(binding.getRoot());
            permViewUtil.performActionWithPermissions(
                    getContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA
                    },
                    this,
                    () -> pictureUtil.openImageMenu()
            );
        });

        return binding.getRoot();
    }

    @Override
    public Context getApplicationContext() {
        return getContext().getApplicationContext();
    }

    @Override
    public void setProgressBar(boolean enable) {
        try {
            if (dialog == null)
                dialog = Util.createProgressDialog(getContext()).show();

            if (enable)
                dialog.show();
            else
                dialog.dismiss();
        } catch (Exception e) {
            Log.e(TAG, "setProgressBar: error hogaya " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void displayError(String reason) {
        Snackbar.make(binding.getRoot(), reason, Snackbar.LENGTH_SHORT)
                .setAction("Retry", v -> {
                })
                .show();
    }

    @Override
    public void openProfile(FirebaseUser user) {
        setupAccount(user);

        Intent intent;
        intent = new Intent(getContext(), HomeActivity.class);
        startActivity(intent);

        getActivity().finish();
    }

    @Override
    public void displayName(String name) {
        binding.nameInput.setText(name);
    }

    @Override
    public void displayProfilePic(String url) {
        if (!TextUtils.isEmpty(url)) {
            Picasso.with(getContext())
                    .load(url)
                    .into(binding.profilePic);
        } else {
            // TODO: 17-11-2016 display text drawable from first letter
        }
    }

    @Override
    public void displayProfilePic(Bitmap image) {
        Util.resizeBitmapTo(image, binding.profilePic.getHeight(), binding.profilePic.getWidth())
                .compose(Util.applyComputationScheduler())
                .doOnNext(bitmap -> binding.profilePic.setImageBitmap(bitmap))
                .subscribe();
    }

    @Override
    public Fragment getFragment() {
        return this;
    }

    @Override
    public void displayProfilePicFromPath(String photoPath) {
        Util.resizeBitmapTo(photoPath,
                binding.profilePic.getHeight(), binding.profilePic.getWidth())
                .compose(Util.applyComputationScheduler())
                .doOnNext(bitmap -> binding.profilePic.setImageBitmap(bitmap))
                .doOnError(throwable -> displayError(throwable.getMessage()))
                .onErrorResumeNext(Observable.empty())
                .subscribe();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        pictureUtil.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        permViewUtil.onPermissionResult(requestCode, permissions, grantResults);
    }

    public void setupAccount(FirebaseUser user) {
        String accountName = user.getDisplayName();
        if (TextUtils.isEmpty(accountName)) {
            accountName = "Tuesday";
        }

        Preferences preferences = Preferences.getInstance(getContext());
        preferences.setLoggedIn(true);
        preferences.setAccountName(accountName);

        Account account = new Account(accountName, SyncUtils.ACCOUNT_TYPE);
        SyncUtils.createSyncAccount(getContext(), account);
    }
}
