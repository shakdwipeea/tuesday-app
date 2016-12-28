package com.shakdwipeea.tuesday.auth;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
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
import com.google.firebase.auth.UserProfileChangeRequest;
import com.shakdwipeea.tuesday.data.Preferences;
import com.shakdwipeea.tuesday.data.entities.user.User;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;

import rx.Observable;

import static com.google.android.gms.internal.zzs.TAG;

/**
 * Created by ashak on 02-10-2016.
 */

public class AuthPresenter {
    AuthContract.View view;

    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;

    public static Observable<String> profilePic;

    private Context context;

    AuthPresenter(AuthContract.View view, Context context) {
        this.view = view;
        this.context = context;
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
            if (firebaseAuth.getCurrentUser() != null) {
                view.openProfile(firebaseAuth.getCurrentUser());
            }

            auth.removeAuthStateListener(authListener);
        };

        auth.addAuthStateListener(authListener);
    }


}
