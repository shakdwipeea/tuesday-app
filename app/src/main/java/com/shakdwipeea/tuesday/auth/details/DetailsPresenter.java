package com.shakdwipeea.tuesday.auth.details;

import com.google.firebase.auth.FirebaseAuth;
import com.shakdwipeea.tuesday.data.api.ApiFactory;
import com.shakdwipeea.tuesday.data.api.ApiService;
import com.shakdwipeea.tuesday.data.entities.user.User;
import com.shakdwipeea.tuesday.util.Util;

/**
 * Created by akash on 20/12/16.
 */

public class DetailsPresenter implements DetailsContract.Presenter {
    private DetailsContract.View detailsView;
    private ApiService apiService;

    private FirebaseAuth firebaseAuth;

    public DetailsPresenter(DetailsContract.View detailsView) {
        this.detailsView = detailsView;
        this.apiService = ApiFactory.getInstance();
        this.firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void saveDetails(User user) {
        apiService.saveDetails(user)
                .compose(Util.applySchedulers())
                .doOnSubscribe(() -> detailsView.displayProgressBar(true))
                .doOnCompleted(() -> detailsView.displayProgressBar(false))
                .subscribe(
                        genResponse -> {
                            detailsView.saveUserDetails(user);
                            signInUser(user);
                        }
                );
    }

    private void signInUser(User user) {
        firebaseAuth.signInWithCustomToken(user.token)
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        detailsView.displayError("Could not sign in you in. " +
                                "Now you stay here for eternity");
                    }
                });
    }
}
