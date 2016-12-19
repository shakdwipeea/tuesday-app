package com.shakdwipeea.tuesday.profile;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.shakdwipeea.tuesday.auth.AuthActivity;
import com.shakdwipeea.tuesday.data.AuthService;
import com.shakdwipeea.tuesday.data.ProfilePicService;
import com.shakdwipeea.tuesday.data.contacts.AddContactService;
import com.shakdwipeea.tuesday.data.entities.user.Provider;
import com.shakdwipeea.tuesday.data.entities.user.User;
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

public class ProfilePresenter implements ProfileContract.Presenter {
    private static final String TAG = "ProfilePresenter";

    private ProfileContract.View profileView;

    private FirebaseUser loggedInUser;
    private String provider;

    private UserService userService;
    private FirebaseService firebaseService;

    private AddContactService addContactService;

    // User whose profile is being displayed
    private User user;

    private Boolean isFriend;

    // The profile being viewed is of the logged in person
    private Boolean isSelf;

    ProfilePresenter(ProfileContract.View profileView) {
        this.profileView = profileView;
        userService = UserService.getInstance();

        addContactService = new AddContactService(profileView.getContext());
    }

    @Override
    public void subscribe(User user) {
        loggedInUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseService = new FirebaseService(user.uid);
        //setupProfile();
        //getTuesID();
        this.user = user;
        loadProfile(user);
        isFriend = false;
    }

    /**
     * Retrieve the profile details from firebase and display it
     * @param providedUser User whose profile is to be displayed
     */
    @Override
    public void loadProfile(User providedUser) {
        firebaseService.getProfile()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(user1 -> user = user1)
                .doOnNext(user1 -> {
                    if (!user1.uid.equals(loggedInUser.getUid())) {
                        profileView.loggedInUser(false);
                        isSelf = false;
                    } else {
                        profileView.loggedInUser(true);
                        isSelf = true;
                    }
                })
                .subscribe(
                        user1 -> profileView.displayUser(user1)
                );

        firebaseService.getProvider()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        providerList -> profileView.addProvider(providerList),
                        Throwable::printStackTrace,
                        () -> Log.e(TAG, "loadProfile: Complete called")
                );

        userService.getTuesContacts()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMapIterable(strings -> strings)
                .filter( s -> s.equals(user.uid))
                .doOnNext(s -> {
                    isFriend = true;
                    profileView.setAddFriendFabIcon(false);
                })
                .subscribe(
                        s -> Log.d(TAG, "loadProfile: Friend uid is " + s),
                        Throwable::printStackTrace
                );
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

    public void toggleContact() {
//        if (!profileView.hasContactPermission()) {
//            return;
//        }

        try {
            if (isFriend) {
                deleteContact();
                //addContactService.deleteContact(user);
            } else {
                saveContact();
                //addContactService.addContact(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
            profileView.displayError(e.getMessage());
        }
    }

    @Override
    public void handleFab() {
        if (isSelf) {
            profileView.launchSetup();
        } else {
            toggleContact();
        }
    }

    private void saveContact() {
        Log.d(TAG, "saveContact: " + user);

        // add to my contacts
        userService.saveTuesContacts(user.uid);

        // add to his added_by
        firebaseService.addSavedBy(loggedInUser.getUid());

        profileView.setAddFriendFabIcon(false);
    }

    private void deleteContact() {
        // remove from my contacts
        userService.removeTuesContact(user.uid);

        // firebase remove saved by
        firebaseService.removeSavedBy(loggedInUser.getUid());

        profileView.setAddFriendFabIcon(true);
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
                .doOnCompleted(() -> {
                    profileView.displayProfilePic(url);
                    userService.setHighResProfilePic(true);
                })
                .subscribe(
                         aVoid ->  {},
                        throwable -> {
                            profileView.displayError(throwable.getMessage());
                            setupProfile();
                        }
                );
    }

    private void setupProfile() {
        if (loggedInUser != null && loggedInUser.getProviders() != null) {
            // display loggedInUser name
            profileView.displayName(loggedInUser.getDisplayName());

            // get auth provider
            provider = loggedInUser.getProviders().get(0);
            Log.d(TAG, "setupProfile: " + provider);

            if (loggedInUser.getPhotoUrl() == null) {
                profileView.displayDefaultPic();
            } else {
                // check if loggedInUser already has a high resolution photo o/w get one
                userService.hasHighResProfilePic()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.newThread())
                        .filter(value -> !value)
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

    public void displayProviderDetails(Provider provider) {
        String providerDetail = "Not available";

        if (provider.getProviderDetails().isPersonal && !isSelf) {
            profileView.showAccessButton(true);
        } else {
            profileView.showAccessButton(false);
            providerDetail = getProviderDetails(provider);
        }
        profileView.displayProviderInfo(provider, providerDetail);

        firebaseService.getAccessedBy(provider.name)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(s -> {
                    if (s.equals(loggedInUser.getUid())) {
                        profileView.showAccessButton(false);
                        profileView.displayProviderInfo(provider, getProviderDetails(provider ));
                    }
                });

    }

    private String getProviderDetails(Provider provider) {
        String providerDetail = "Default value";

        switch (provider.getProviderDetails().getType()) {
            case PHONE_NUMBER_NO_VERIFICATION:
            case PHONE_NUMBER_VERIFICATION:
                providerDetail = provider.getProviderDetails().phoneNumber;
                break;
            case USERNAME_NO_VERIFICATION:
                providerDetail = provider.getProviderDetails().username;
                break;
        }
        return providerDetail;
    }

    public void requestAccess(Provider provider) {
        Log.d(TAG, "requestAccess: " + provider);
        firebaseService.addRequestedBy(provider.name, loggedInUser.getUid());
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
