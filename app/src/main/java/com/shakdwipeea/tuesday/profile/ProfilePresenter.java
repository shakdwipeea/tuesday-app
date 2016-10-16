package com.shakdwipeea.tuesday.profile;

import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.shakdwipeea.tuesday.api.AuthService;
import com.shakdwipeea.tuesday.auth.AuthActivity;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by ashak on 15-10-2016.
 */

class ProfilePresenter implements ProfileContract.Presenter {
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

    @Override
    public void changeProfilePic() {
        profileView.openImageMenu();
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
                displayPic(AuthService.getFbProfilePic());
            } else if (provider.equals(AuthService.GOOGLE_AUTH_PROVIDER)) {
                displayPic(AuthActivity.AuthMode.GOOGLE_AUTH);
            } else if (provider.equals(AuthService.TWITTER_AUTH_PROVIDER)) {
                displayPic(AuthActivity.AuthMode.TWITTER_AUTH);
            }
        } else {
            profileView.displayError("You are not logged in.");
        }
    }

    /**
     * used for fb and twitter AuthService provides Observables with gives the profile pic,
     * this function uses that observable to display the profile pic
     * @param profilePicObservable Observable that provides url for high res profile pic
     */
    private void displayPic(Observable<String> profilePicObservable) {
        profilePicObservable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                        url -> profileView.displayProfilePic(url),
                        Throwable::printStackTrace
                );
    }

    /**
     * used in case of google, twitter displays the profile pic by
     * transforming existing low res url
     */
    private void displayPic(AuthActivity.AuthMode authMode) {
        String lowResUrl = user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : null;
        if (lowResUrl != null) {
            profileView.displayProfilePic(getHighResUrl(lowResUrl, authMode));
        }
    }


    private String getHighResUrl(String lowResUrl, AuthActivity.AuthMode authMode) {
        switch (authMode) {
            case GOOGLE_AUTH: return parseGoogleUrl(lowResUrl);
            case TWITTER_AUTH: return parseTwitterUrl(lowResUrl);
            default: return null;
        }
    }

    /**
     * the profile pic url in case of twitter is sth like
     * http://pbs.twimg.com/profile_images/463646119960920064/_lMH5iFt_normal.jpeg
     *
     * Omit the underscore and variant to retrieve the original image.
     * NOTE THE IMAGE CAN BE VERY LARGE
     *
     * @param lowResUrl the default low res image
     * @return highResUrl
     */
    private String parseTwitterUrl(String lowResUrl) {
        return lowResUrl.replace("_normal", "");
    }

    /**
     *  google returns the profile url as
     *  https://lh5.googleusercontent.com/-GoXxObG2mVE/AAAAAAAAAAI/AAAAAAAABFY/PzVrrZdkQYQ/s96-c/photo.jpg
     *  here 96 is the width and height, to get a full res we can put our required dimensions
     *  and request
     *
     *  @param lowResUrl The default low res url
     */
    private String parseGoogleUrl(String lowResUrl) {
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
