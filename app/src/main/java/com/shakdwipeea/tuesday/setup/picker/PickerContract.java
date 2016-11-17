package com.shakdwipeea.tuesday.setup.picker;

/**
 * Created by ashak on 11-11-2016.
 */

public interface PickerContract {
    interface View {
        void displayError(String reason);
        void displayProgress(Boolean show);
    }

    interface Presenter {
        void subscribe();
        void unsubscribe();
    }
}
