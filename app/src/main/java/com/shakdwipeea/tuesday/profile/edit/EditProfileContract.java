package com.shakdwipeea.tuesday.profile.edit;

import com.shakdwipeea.tuesday.data.entities.user.Provider;
import com.shakdwipeea.tuesday.data.entities.user.ProviderDetails;
import com.shakdwipeea.tuesday.data.entities.user.User;

import java.util.List;

/**
 * Created by akash on 24/1/17.
 */

public class EditProfileContract {
    interface View {
        void displayError(String reason);

        void addProvider(ProviderDetails providerDetails);
        void clearProvider();

        void addCallDetails(ProviderDetails callDetails);
        void clearCallDetails();

        void addMailDetails(ProviderDetails mailDetails);
        void clearMailDetails();

        void displayProgress(boolean enable);
    }

    interface Presenter {
        void loadProviders();
        void subscribe();
        void unSubscribe();
    }
}
