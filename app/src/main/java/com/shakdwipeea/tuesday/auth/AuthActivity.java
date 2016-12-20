package com.shakdwipeea.tuesday.auth;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseUser;
import com.shakdwipeea.tuesday.R;
import com.shakdwipeea.tuesday.auth.phone.PhoneInputFragment;
import com.shakdwipeea.tuesday.data.Preferences;
import com.shakdwipeea.tuesday.databinding.ActivityAuthBinding;
import com.shakdwipeea.tuesday.home.HomeActivity;
import com.shakdwipeea.tuesday.setup.picker.ProviderPickerActivity;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import io.fabric.sdk.android.Fabric;

public class AuthActivity extends AppCompatActivity implements AuthContract.View {
    private static final String TAG = "AuthActivity";

    private AuthPresenter authPresenter;
    private ActivityAuthBinding binding;

    // a hack for not allowing the launch of ProfileActivity twice
    private boolean profileLaunched;

    @Override
    public void onStart() {
        super.onStart();
        authPresenter.subscribe();
    }

    @Override
    public void onStop() {
        super.onStop();
        authPresenter.unSubscribe();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_auth);

        authPresenter = new AuthPresenter(this, this);
        binding.setVm(authPresenter);

        profileLaunched = false;

        loadFragment(new PhoneInputFragment());
    }

    public void loadFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.auth_fragment_container, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void displayError(String message) {
        Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void openProfile(FirebaseUser user) {
        // TODO: 15-10-2016 investigate why the auth changed is called twice
        if (!profileLaunched) {
            profileLaunched = true;
//            Intent intent = new Intent(this, ProfileActivity.class);
//            if (user.getPhotoUrl() != null)
//                intent.putExtra(ProfileActivity.PROFILE_IMAGE_EXTRA, user.getPhotoUrl().toString());
//            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

            Intent intent;
            if (Preferences.getInstance(this).isSetupComplete()) {
                intent = new Intent(this, HomeActivity.class);
            } else {
                intent = new Intent(this, ProviderPickerActivity.class);
            }

            startActivity(intent);
            finish();
        }
    }
}
