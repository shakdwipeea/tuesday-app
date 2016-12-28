package com.shakdwipeea.tuesday.auth.details;

import android.net.Uri;
import android.text.TextUtils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.shakdwipeea.tuesday.data.Preferences;
import com.shakdwipeea.tuesday.data.api.ApiFactory;
import com.shakdwipeea.tuesday.data.api.ApiService;
import com.shakdwipeea.tuesday.data.entities.user.User;
import com.shakdwipeea.tuesday.picture.ProfilePicturePresenter;
import com.shakdwipeea.tuesday.util.Util;

/**
 * Created by akash on 20/12/16.
 */

public class DetailsPresenter extends ProfilePicturePresenter implements DetailsContract.Presenter {
    private DetailsContract.View detailsView;
    private ApiService apiService;

    private FirebaseAuth firebaseAuth;

    public DetailsPresenter(DetailsContract.View detailsView) {
        super(detailsView);
        this.detailsView = detailsView;
        this.apiService = ApiFactory.getInstance();
        this.firebaseAuth = FirebaseAuth.getInstance();
        showProfile();
    }

    public void showProfile() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            detailsView.displayName(firebaseUser.getDisplayName());

            if (firebaseUser.getPhotoUrl() != null)
                detailsView.displayProfilePic(firebaseUser.getPhotoUrl().toString());
        }
    }

    @Override
    public void saveDetails(User user) {
        apiService.saveDetails(user)
                .compose(Util.applySchedulers())
                .doOnSubscribe(() -> detailsView.setProgressBar(true))
                .doOnCompleted(() -> detailsView.setProgressBar(false))
                .subscribe(
                        genResponse -> {
                            updateFirebaseUser(user);
                        }
                );
    }

    private void updateFirebaseUser(User user) {
        UserProfileChangeRequest.Builder builder = new UserProfileChangeRequest.Builder()
                .setDisplayName(user.name);

//        if (!TextUtils.isEmpty(userDetails.pic)) {
//            builder.setPhotoUri(Uri.parse(userDetails.pic));
//        }

        if (firebaseAuth.getCurrentUser() != null) {
            firebaseAuth.getCurrentUser().updateProfile(builder.build());
            detailsView.openProfile(firebaseAuth.getCurrentUser());
        }
    }
}
