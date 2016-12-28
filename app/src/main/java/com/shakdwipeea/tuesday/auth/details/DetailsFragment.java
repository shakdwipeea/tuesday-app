package com.shakdwipeea.tuesday.auth.details;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.auth.FirebaseUser;
import com.shakdwipeea.tuesday.R;
import com.shakdwipeea.tuesday.data.PermConstants;
import com.shakdwipeea.tuesday.data.Preferences;
import com.shakdwipeea.tuesday.data.entities.user.User;
import com.shakdwipeea.tuesday.databinding.FragmentDetailsBinding;
import com.shakdwipeea.tuesday.home.HomeActivity;
import com.shakdwipeea.tuesday.picture.ProfilePictureUtil;
import com.shakdwipeea.tuesday.setup.picker.ProviderPickerActivity;
import com.shakdwipeea.tuesday.util.Util;
import com.squareup.picasso.Picasso;

import rx.Observable;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailsFragment extends Fragment implements DetailsContract.View {
    public static final String KEY_TOKEN = "token";
    public static final String KEY_PHONE = "phone";

    FragmentDetailsBinding binding;

    DetailsPresenter presenter;

    MaterialDialog dialog;

    ProfilePictureUtil pictureUtil;

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

        pictureUtil = new ProfilePictureUtil(presenter, this);

        String token = getArguments().getString(KEY_TOKEN);
        String phone = getArguments().getString(KEY_PHONE);

        binding.saveButton.setOnClickListener(v -> {
            User user = new User();
            user.name = binding.nameInput.getText().toString();
            user.token = token;
            user.phoneNumber = phone;
            presenter.saveDetails(user);
        });

        binding.cameraIcon.setOnClickListener(v -> {
            pictureUtil.openImageMenu();
        });

        return binding.getRoot();
    }

    @Override
    public Context getApplicationContext() {
        return getContext().getApplicationContext();
    }

    @Override
    public void setProgressBar(boolean enable) {
        if (dialog == null)
            dialog = Util.createProgressDialog(getContext()).show();

        if (enable)
            dialog.show();
        else
            dialog.dismiss();
    }

    @Override
    public void displayError(String reason) {
        Snackbar.make(binding.getRoot(), reason, Snackbar.LENGTH_SHORT)
                .show();
    }

    @Override
    public void saveUserDetails(User user) {
        Preferences.getInstance(getContext())
                .setUserDetails(user);
    }

    @Override
    public void openProfile(FirebaseUser user) {
        Intent intent;
        if (Preferences.getInstance(getContext()).isSetupComplete()) {
            intent = new Intent(getContext(), HomeActivity.class);
        } else {
            intent = new Intent(getContext(), ProviderPickerActivity.class);
        }

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
        switch (requestCode) {
            case PermConstants.REQUEST_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pictureUtil.openCamera();
                } else {
                    displayError("Cannot save photo then");
                }
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
