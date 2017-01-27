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

        void addProvider(Provider provider);
        void clearProvider();

        void addCallDetails(Provider call);
        void clearCallDetails();

        void addMailDetails(Provider mail);
        void clearMailDetails();

        void displayProgress(boolean enable);
    }

    interface Presenter {
        void loadProviders();
        void subscribe();
        void unSubscribe();
    }

    interface ItemPresenter {
        void saveDetails(Provider provider);
        void deleteDetail(Provider provider);
    }
}
