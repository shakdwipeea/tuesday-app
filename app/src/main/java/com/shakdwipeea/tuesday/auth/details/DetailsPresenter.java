package com.shakdwipeea.tuesday.auth.details;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.shakdwipeea.tuesday.R;
import com.shakdwipeea.tuesday.data.api.ApiFactory;
import com.shakdwipeea.tuesday.data.api.ApiService;
import com.shakdwipeea.tuesday.data.entities.user.Provider;
import com.shakdwipeea.tuesday.data.entities.user.ProviderDetails;
import com.shakdwipeea.tuesday.data.entities.user.User;
import com.shakdwipeea.tuesday.data.firebase.UserService;
import com.shakdwipeea.tuesday.data.providers.ProviderNames;
import com.shakdwipeea.tuesday.data.providers.ProviderService;
import com.shakdwipeea.tuesday.picture.ProfilePicturePresenter;
import com.shakdwipeea.tuesday.util.Util;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by akash on 20/12/16.
 */

public class DetailsPresenter extends ProfilePicturePresenter implements DetailsContract.Presenter {
    private static final String TAG = "DetailsPresenter";

    private DetailsContract.View detailsView;
    private ApiService apiService;

    private FirebaseAuth firebaseAuth;
    private CompositeSubscription compositeSubscription;

    public DetailsPresenter(DetailsContract.View detailsView) {
        super(detailsView);
        this.detailsView = detailsView;
        this.apiService = ApiFactory.getInstance();
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.compositeSubscription = new CompositeSubscription();
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
        detailsView.setProgressBar(true);
        Subscription subscription = apiService.saveDetails(user)
                .compose(Util.applySchedulers())
                .subscribe(
                        genResponse -> {
                            detailsView.setProgressBar(false);
                            updateFirebaseAuthser(user);
                        },
                        throwable -> {
                            detailsView.displayError(throwable.getMessage());
                            throwable.printStackTrace();
                        }
                );
        compositeSubscription.add(subscription);
    }

    private void updateFirebaseAuthser(User user) {
        UserProfileChangeRequest.Builder builder = new UserProfileChangeRequest.Builder()
                .setDisplayName(user.name);

//        if (!TextUtils.isEmpty(userDetails.pic)) {
//            builder.setPhotoUri(Uri.parse(userDetails.pic));
//        }

        if (firebaseAuth.getCurrentUser() != null) {
            firebaseAuth.getCurrentUser()
                    .updateProfile(builder.build())
                    .addOnSuccessListener(command -> {
                        Log.d(TAG, "updateFirebaseAuthser: Firebase auth profile updated");
                        saveUserToFirebase(user);
                        detailsView.openProfile(firebaseAuth.getCurrentUser());
                    })
                    .addOnFailureListener(command -> {
                        Log.d(TAG, "updateFirebaseAuthser: Firebase auth profile not updated");
                        command.printStackTrace();
                        detailsView.displayError(getPictureView().getContext()
                                .getString(R.string.details_view_error));
                    });
        }
    }

    private void saveUserToFirebase(User user) {
        Log.d(TAG, "saveUserToFirebase: User has this data " + user);
        Provider provider = ProviderService.getInstance()
                .getProviderHashMap()
                .get(ProviderNames.Call);

        provider.providerDetails.phoneNumber = user.phoneNumber;
        provider.providerDetails.isPersonal = false;
        provider.providerDetails.detailType = ProviderDetails.DetailType.PRIMARY;

        UserService userService = new UserService();
        userService.setName(user.name);
        userService.saveProvider(provider);
    }

    public void unsubscribe() {
        compositeSubscription.clear();
    }
}
