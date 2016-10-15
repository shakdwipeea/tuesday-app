package com.shakdwipeea.tuesday.profile;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.shakdwipeea.tuesday.api.AuthService;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by ashak on 15-10-2016.
 */

public class ProfilePresenter implements ProfileContract.Presenter {
    private ProfileContract.View profileView;

    ProfilePresenter(ProfileContract.View profileView) {
        this.profileView = profileView;
    }

    @Override
    public void subscribe() {
        getHighResProfilePic();
    }

    private void getHighResProfilePic() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && user.getProviders() != null) {
            // display user name
            profileView.displayName(user.getDisplayName());

            // get auth provider
            String provider = user.getProviders().get(0);
            if (provider.equals(AuthService.FACEBOOK_AUTH_PROVIDER)) {
                //get profile pic from fb
                getPicFromFb();
            }
        } else {
            profileView.displayError("You are not logged in.");
        }
    }

    private void getPicFromFb() {
        AuthService.getFbProfilePic()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                        url -> profileView.displayProfilePic(url),
                        Throwable::printStackTrace
                );
    }
}
