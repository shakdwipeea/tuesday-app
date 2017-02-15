package com.shakdwipeea.tuesday.profile.view;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.shakdwipeea.tuesday.data.contacts.AddContactService;
import com.shakdwipeea.tuesday.data.entities.user.Provider;
import com.shakdwipeea.tuesday.data.entities.user.User;
import com.shakdwipeea.tuesday.data.firebase.FirebaseService;
import com.shakdwipeea.tuesday.data.firebase.UserService;
import com.shakdwipeea.tuesday.data.providers.ProviderNames;
import com.shakdwipeea.tuesday.picture.ProfilePicturePresenter;

import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by ashak on 15-10-2016.
 */

public class ProfilePresenter extends ProfilePicturePresenter implements ProfileContract.Presenter {

    private static final String TAG = "ProfilePresenter";

    private ProfileContract.View profileView;

    private FirebaseUser loggedInUser;

    private UserService userService;
    private FirebaseService firebaseService;

    private AddContactService addContactService;

    // User whose profile is being displayed
    private User user;

    private Boolean isFriend;

    // The profile being viewed is of the logged in person
    private Boolean isSelf;

    // TODO: 2/1/17 special snowflake
    private String phoneNumber;

    ProfilePresenter(ProfileContract.View profileView) {
        super(profileView);

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
        phoneNumber = user.phoneNumber;

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
                .doOnNext(user1 -> {
                    user = user1;
                    user.phoneNumber = phoneNumber;
                })
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
                .doOnNext(this::processProviders)
                .subscribe(
                        providerList -> {},
                        Throwable::printStackTrace,
                        () -> Log.e(TAG, "loadProfile: Complete called")
                );

        userService.getTuesContactsWithTags()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(friendList -> {
                    if (friendList.get(user.uid) != null) {
                        isFriend = true;
                        profileView.setAddFriendFabIcon(false);
                        profileView.setFriendTag(friendList.get(user.uid));
                    }
                })
                .subscribe(
                        s -> Log.d(TAG, "loadProfile: Friend uid is " + s),
                        Throwable::printStackTrace
                );
    }

    /**
     * Extract out the call and email type
     *
     * @param providerList The entire providerList
     */
    private void processProviders(List<Provider> providerList) {
        Observable.from(providerList)
                .doOnSubscribe(() -> {
                    profileView.clearCallDetails();
                    profileView.clearMailDetails();
                })
                .filter(provider -> {
                    switch (provider.getName()) {
                        case ProviderNames.Email:
                            profileView.addMailDetails(provider.providerDetails);
                            return false;

                        case ProviderNames.Call:
                            Log.d(TAG, "processProviders: Adding " + provider.providerDetails);
                            profileView.addCallDetails(provider.providerDetails);
                            return false;

                        default: return true;
                    }
                })
                .toList()
                .subscribe(
                        providerList1 ->  profileView.addProvider(providerList1),
                        Throwable::printStackTrace
                );
    }


    public void toggleContact() {
//        if (!profileView.hasContactPermission()) {
//            return;
//        }
        Log.d(TAG, "toggleContact: for " + user);

        // TODO: 2/1/17 handle case in which contact permission is not there
        try {
            if (isFriend) {
                deleteContact();
                addContactService.deleteContact(user);
            } else {
                saveContact();
                addContactService.addContact(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
            profileView.displayError(e.getMessage());
        }
    }

    @Override
    public void changeName(String name) {
        UserProfileChangeRequest changeRequest = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();

        loggedInUser.updateProfile(changeRequest);
        userService.setName(name);

        profileView.displayName(name);
    }

    @Override
    public void handleFab() {
        if (isSelf) {
            profileView.launchSetup();
        } else {
            toggleContact();
        }
    }

    public void saveTag(String tag) {
        userService.saveTuesContacts(user.uid, tag);
    }

    private void saveContact() {
        Log.d(TAG, "saveContact: " + user);

        // add to my contacts
        userService.saveTuesContacts(user.uid, profileView.getFriendTag());

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
}
