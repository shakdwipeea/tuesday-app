package com.shakdwipeea.tuesday.auth;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
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
import com.shakdwipeea.tuesday.data.Preferences;
import com.shakdwipeea.tuesday.databinding.ActivityAuthBinding;
import com.shakdwipeea.tuesday.home.HomeActivity;
import com.shakdwipeea.tuesday.setup.picker.ProviderPickerActivity;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import io.fabric.sdk.android.Fabric;

public class AuthActivity extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener, AuthContract.View {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "x59bXU8G7xGYRtRZeA7BarNEg";
    private static final String TWITTER_SECRET = "iQ7SvDY0QOi9XIFbbwtrJVY955nD48OSSG3xI5mc49QQbvCH8G";


    private static final int RC_SIGN_IN = 1234;

    // different modes of sign in to determine where to pass the onActivityResult
    // in case of google auth we are explicitly launching the activity and setting
    // the request code there
    public enum AuthMode {
        FACEBOOK_AUTH,
        TWITTER_AUTH,
        GOOGLE_AUTH
    }

    AuthMode authMode;

    private static final String TAG = "AuthActivity";

    private AuthPresenter authPresenter;
    private ActivityAuthBinding binding;

    //Google
    private GoogleApiClient googleApiClient;

    //Facebook
    private CallbackManager callbackManager;

    // a hack for not allowing the launch of ProfileActivity twice
    private boolean profileLaunched;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));

        profileLaunched = false;

        // configure facebook login
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        binding = DataBindingUtil.setContentView(this, R.layout.activity_auth);

        authPresenter = new AuthPresenter(this);
        binding.setVm(authPresenter);

        showHashKey(this);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_app_oauth_id))
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        //facebook login
        binding.fbLoginButton.setReadPermissions("email", "public_profile");
        binding.fbLoginButton.registerCallback(callbackManager, authPresenter);

        //twitter
        binding.twitterLoginButton.setCallback(authPresenter);
    }

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

    public void openGoogleLogin() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void openFacebookLogin() {
        authMode = AuthMode.FACEBOOK_AUTH;
        binding.fbLoginButton.performClick();
    }

    @Override
    public void openTwitterLogin() {
        authMode = AuthMode.TWITTER_AUTH;
        binding.twitterLoginButton.performClick();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                authPresenter.firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                // ...
                displayError("Could not authorize you.");
            }
        } else if (authMode == AuthMode.TWITTER_AUTH){
            binding.twitterLoginButton.onActivityResult(requestCode, resultCode, data);
        } else if (authMode == AuthMode.FACEBOOK_AUTH) {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        displayError("Google Play Services error.");
    }

    public  void showHashKey(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo("com.shakdwipeea.tuesday",
                    PackageManager.GET_SIGNATURES);
            for (android.content.pm.Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());

                String sign= Base64.encodeToString(md.digest(), Base64.DEFAULT);
                Log.e("KeyHash:", sign);
                //  Toast.makeText(getApplicationContext(),sign,     Toast.LENGTH_LONG).show();
            }
            Log.d("KeyHash:", "****------------***");
        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}
