package com.shakdwipeea.tuesday.profile.edit;

import com.shakdwipeea.tuesday.data.entities.user.Provider;
import com.shakdwipeea.tuesday.data.entities.user.ProviderDetails;

/**
 * Created by akash on 24/1/17.
 */

public class EditProfileItemViewModel {
    private static final String TAG = "EditProfileItemViewMode";

    private EditProfileContract.ItemPresenter itemPresenter;
    private Provider provider;

    public EditProfileItemViewModel(EditProfileContract.ItemPresenter itemPresenter,
                                    Provider provider) {
        this.itemPresenter = itemPresenter;
        this.provider = provider;
    }

    public String getDetail(ProviderDetails providerDetails) {
        switch (providerDetails.getType()) {
            case PHONE_NUMBER_VERIFICATION:
            case PHONE_NUMBER_NO_VERIFICATION:
                return providerDetails.getPhoneNumber();

            case USERNAME_NO_VERIFICATION:
            case API_VERIFICATION:
                return providerDetails.getUsername();

            default: return providerDetails.getUsername();
        }
    }


}