package com.shakdwipeea.tuesday.auth;

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
import com.google.firebase.auth.TwitterAuthProvider;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;

import rx.Observable;

import static com.google.android.gms.internal.zzs.TAG;

/**
 * Created by ashak on 02-10-2016.
 */

public class AuthPresenter extends Callback<TwitterSession> implements FacebookCallback<LoginResult> {
    AuthContract.View view;

    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;

    public static Observable<String> profilePic;

    AuthPresenter(AuthContract.View view) {
        this.view = view;
    }

    void subscribe() {
        init();
    }

    void unSubscribe() {
        if (authListener != null) auth.removeAuthStateListener(authListener);
    }

    private void init() {
        //get firebase instance
        auth = FirebaseAuth.getInstance();

        //listen to firebase auth changes
        authListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            Log.d(TAG, "init: User is " + user);
            if (user != null) {
                // User is signed in
                Log.e(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                view.openProfile(user);

                // when profile page is open we don't need this
                auth.removeAuthStateListener(authListener);
            } else {
                // User is signed out
                Log.d(TAG, "onAuthStateChanged:signed_out");
            }

        };

        auth.addAuthStateListener(authListener);
    }

    void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
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

                });
    }

    private void handleTwitterSession(TwitterSession session) {
        Log.d(TAG, "handleTwitterSession:" + session);

        AuthCredential credential = TwitterAuthProvider.getCredential(
                session.getAuthToken().token,
                session.getAuthToken().secret);

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
                });
    }

    public void googleSignIn(View view) {
        this.view.openGoogleLogin();
    }

    public void facebookSignIn(View view) {
        this.view.openFacebookLogin();
    }

    public void twitterSignIn() {
        view.openTwitterLogin();
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


    // twitter callback methods
    @Override
    public void success(Result<TwitterSession> result) {
        handleTwitterSession(result.data);
    }

    @Override
    public void failure(TwitterException exception) {
        exception.printStackTrace();
        view.displayError(exception.getMessage());
    }
}
