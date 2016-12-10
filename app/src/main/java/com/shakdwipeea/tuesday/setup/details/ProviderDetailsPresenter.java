package com.shakdwipeea.tuesday.setup.details;

import com.shakdwipeea.tuesday.data.entities.user.Provider;
import com.shakdwipeea.tuesday.data.firebase.UserService;

/**
 * Created by ashak on 11-11-2016.
 */

public class ProviderDetailsPresenter implements ProviderDetailsContract.Presenter {
    private ProviderDetailsContract.View providerDetailsView;
    private UserService userService;
    private Provider provider;

    public ProviderDetailsPresenter(ProviderDetailsContract.View providerDetailsView
            , Provider provider) {
        this.providerDetailsView = providerDetailsView;
        this.provider = provider;
        this.userService = UserService.getInstance();
    }

    @Override
    public void saveProviderDetails() {
        // TODO: 11-11-2016 from string resource
        providerDetailsView.changeButtonText("Saving");
        userService.saveProvider(provider);
        providerDetailsView.changeButtonText("Save");
        //providerDetailsView.loadNextProvider();
    }
}
