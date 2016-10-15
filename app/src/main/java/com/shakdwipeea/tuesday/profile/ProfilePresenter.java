package com.shakdwipeea.tuesday.profile;

import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.shakdwipeea.tuesday.api.AuthService;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by ashak on 15-10-2016.
 */

public class ProfilePresenter implements ProfileContract.Presenter {
    private static final String TAG = "ProfilePresenter";

    private ProfileContract.View profileView;

    private FirebaseUser user;

    ProfilePresenter(ProfileContract.View profileView) {
        this.profileView = profileView;
    }

    @Override
    public void subscribe() {
        getHighResProfilePic();
    }

    private void getHighResProfilePic() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && user.getProviders() != null) {
            // display user name
            profileView.displayName(user.getDisplayName());

            // get auth provider
            String provider = user.getProviders().get(0);
            Log.d(TAG, "getHighResProfilePic: " + provider);

            if (provider.equals(AuthService.FACEBOOK_AUTH_PROVIDER)) {
                //get profile pic from fb
                getPicFromFb();
            } else if (provider.equals(AuthService.GOOGLE_AUTH_PROVIDER)) {
                getPicFromGoogle();
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

    private void getPicFromGoogle() {
        String lowResUrl = user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : null;
        if (lowResUrl != null) {
            profileView.displayProfilePic(getHighResUrl(lowResUrl));
        }
    }

    /**
     *  google returns the profile url as
     *  https://lh5.googleusercontent.com/-GoXxObG2mVE/AAAAAAAAAAI/AAAAAAAABFY/PzVrrZdkQYQ/s96-c/photo.jpg
     *  here 96 is the width and height, to get a full res we can put our required dimensions
     *  and request
     *
     *  @param lowResUrl The default low res url
     */
    private String getHighResUrl(String lowResUrl) {
        // find the dimension and change it to 400dp
        String[] urlParts = lowResUrl.split("/");
        String dimPart = urlParts[urlParts.length - 2];
        String[] dimValue = dimPart.split("-");
        dimValue[0] = "s400";

        // recreate the url
        urlParts[urlParts.length - 2] = TextUtils.join("-", dimValue);
        return TextUtils.join("/", urlParts);
    }
}
