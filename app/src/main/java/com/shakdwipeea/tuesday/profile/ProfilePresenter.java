package com.shakdwipeea.tuesday.profile;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.shakdwipeea.tuesday.auth.AuthActivity;
import com.shakdwipeea.tuesday.data.AuthService;
import com.shakdwipeea.tuesday.data.ProfilePicService;
import com.shakdwipeea.tuesday.data.entities.User;
import com.shakdwipeea.tuesday.data.firebase.FirebaseService;
import com.shakdwipeea.tuesday.data.firebase.UserService;
import com.shakdwipeea.tuesday.util.Util;

import java.io.FileNotFoundException;

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
    private String provider;

    private UserService userService;
    private FirebaseService firebaseService;

    ProfilePresenter(ProfileContract.View profileView) {
        this.profileView = profileView;
        userService = UserService.getInstance();
        firebaseService = new FirebaseService();
    }

    @Override
    public void subscribe() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        setupProfile();
        getTuesID();
    }

    /**
     * Retrieve the profile details from firebase and display it
     * @param user User whose profile is to be displayed
     */
    @Override
    public void loadProfile(User user) {
        firebaseService.getProfile(user.uid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        user1 -> profileView.displayUser(user1)
                );
    }

    private void getTuesID() {
        // new tuesid can right now only be requested from HomeActivity
        userService.getTuesId()
                .doOnNext(tuesId -> profileView.displayTuesId(tuesId))
                .subscribe(
                        tuesId -> {},
                        throwable -> {
                            profileView.displayError(throwable.getMessage());
                            throwable.printStackTrace();
                        }
                );
    }

    @Override
    public void logout() {
        FirebaseAuth.getInstance().signOut();

        // explicit sign out from facebook
        if (provider.equals(AuthService.FACEBOOK_AUTH_PROVIDER))
            LoginManager.getInstance().logOut();

        profileView.launchAuth();
    }

    @Override
    public void updateProfilePic(Context context, Uri imageUri) {
        try {
            // Get the stream to get the bitmap and display the image
            Bitmap bitmap = BitmapFactory.decodeStream(
                    Util.getInputStreamFromFileUri(context, imageUri)
            );
            profileView.displayProfilePic(bitmap);

            // get the stream again, to upload and set as profile pic
            updateProfilePicFromObject(Util.getInputStreamFromFileUri(context, imageUri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            profileView.displayError(e.getMessage());
            setupProfile();
        }
    }

    /**
     * click handler to delete the profile picture
     */
    @Override
    public void deleteProfilePic() {
        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                .setPhotoUri(null)
                .build();

        profileView.setProgressBar(true);
        userService.updateProfile(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnCompleted(() -> {
                    setupProfile();
                    profileView.setProgressBar(false);
                })
                .subscribe(
                        aVoid -> {},
                        throwable -> {
                            profileView.setProgressBar(false);
                            profileView.displayError(throwable.getMessage());
                        }
                );
    }

    @Override
    public void updateProfilePic(String filePath) {
        updateProfilePicFromObject(filePath);
        profileView.displayProfilePicFromPath(filePath);
    }

    private void updateProfilePicFromObject(Object file) {
        ProfilePicService.saveProfilePic(file)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .map(ProfilePicService::transformUrl)
                .subscribe(
                        this::saveProfilePicture,
                        throwable -> {
                            Log.e(TAG, "updateProfilePic: ", throwable);
                            profileView.displayError(throwable.getMessage());
                            setupProfile();
                        }
                );
    }

    private void saveProfilePicture(String url) {
        UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                .setPhotoUri(Uri.parse(url))
                .build();

        userService.updateProfile(profileChangeRequest)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnCompleted(() -> userService.setHighResProfilePic(true))
                .subscribe(
                        aVoid ->  {},
                        throwable -> {
                            profileView.displayError(throwable.getMessage());
                            setupProfile();
                        }
                );
    }

    private void setupProfile() {
        if (user != null && user.getProviders() != null) {
            // display user name
            profileView.displayName(user.getDisplayName());

            // get auth provider
            provider = user.getProviders().get(0);
            Log.d(TAG, "setupProfile: " + provider);

            if (user.getPhotoUrl() == null) {
                profileView.displayDefaultPic();
            } else {
                // check if user already has a high resolution photo o/w get one
                userService.hasHighResProfilePic()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.newThread())
                        .filter(value -> !value)
                        .doOnNext(aBoolean -> getHighResPicFromProvider())
                        .subscribe(
                                aBoolean -> {},
                                throwable -> {
                                    throwable.printStackTrace();
                                    profileView.displayError(throwable.getMessage());
                                }
                        );
            }
        } else {
            profileView.displayError("You are not logged in.");
        }
    }

    /**
     * Get high res photo based on provider
     */
    private void getHighResPicFromProvider() {
        if (provider.equals(AuthService.FACEBOOK_AUTH_PROVIDER)) {
            displayPic(AuthService.getFbProfilePic());
        } else if (provider.equals(AuthService.GOOGLE_AUTH_PROVIDER)) {
            displayPic(AuthActivity.AuthMode.GOOGLE_AUTH);
        } else if (provider.equals(AuthService.TWITTER_AUTH_PROVIDER)) {
            displayPic(AuthActivity.AuthMode.TWITTER_AUTH);
        }
    }

    /**
     * used for fb  AuthService provides Observables with gives the profile pic,
     * this function uses that observable to display the profile pic
     * @param profilePicObservable Observable that provides url for high res profile pic
     */
    private void displayPic(Observable<String> profilePicObservable) {
        profilePicObservable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                        url -> {
                            saveProfilePicture(url);
                            profileView.displayProfilePic(url);
                        },
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
            String highResUrl = getHighResUrl(lowResUrl, authMode);
            saveProfilePicture(highResUrl);
            profileView.displayProfilePic(highResUrl);
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
     * todo use cloudinary stuff instead
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
     *  todo use cloudinary stuff instead
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
