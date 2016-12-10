package com.shakdwipeea.tuesday.setup.details;

/**
 * Created by ashak on 11-11-2016.
 */

public interface ProviderDetailsContract {
    interface Presenter {
        void saveProviderDetails();
    }

    interface View {
        void displayError(String reason);
        void changeButtonText(String text);
    }
}
