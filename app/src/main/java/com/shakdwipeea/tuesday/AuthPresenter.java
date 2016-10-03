package com.shakdwipeea.tuesday;

import android.util.Log;
import android.view.View;

import com.facebook.AccessToken;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import static com.google.android.gms.internal.zzs.TAG;

/**
 * Created by ashak on 02-10-2016.
 */

public class AuthPresenter implements FacebookCallback<LoginResult> {
    AuthContract.View view;

    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;

    public AuthPresenter(AuthContract.View view) {
        this.view = view;
    }

    public void subscribe() {
        init();
        auth.addAuthStateListener(authListener);
    }

    public void unSubscribe() {
        if (authListener != null) auth.removeAuthStateListener(authListener);
    }

    public void init() {
        //get firebase instance
        auth = FirebaseAuth.getInstance();

        //listen to firebase auth changes
        authListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                // User is signed in
                Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
            } else {
                // User is signed out
                Log.d(TAG, "onAuthStateChanged:signed_out");
            }

        };
    }


    public void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                    // If sign in fails, display a message to the user. If sign in succeeds
                    // the auth state listener will be notified and logic to handle the
                    // signed in user can be handled in the listener.
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "signInWithCredential", task.getException());
                        view.displayError("Authentication Failed");
                    }
                });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        auth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                    // If sign in fails, display a message to the user. If sign in succeeds
                    // the auth state listener will be notified and logic to handle the
                    // signed in user can be handled in the listener.
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "signInWithCredential", task.getException());
                        view.displayError("Authentication failed.");
                    }

                    // ...
                });
    }

    public void googleSignIn(View view) {
        this.view.openGoogleLogin();
    }

    public void facebookSignIn(View view) {
        this.view.openFacebookLogin();
    }

    // Facebook callback methods

    @Override
    public void onSuccess(LoginResult loginResult) {
        Log.d(TAG, "Login Result " + loginResult);
        handleFacebookAccessToken(loginResult.getAccessToken());
    }

    @Override
    public void onCancel() {
        view.displayError("Authorization cancelled");
    }

    @Override
    public void onError(FacebookException error) {
        view.displayError(error.getMessage());
    }
}
