package com.shakdwipeea.tuesday.setup;

import android.view.View;

import com.shakdwipeea.tuesday.data.entities.Provider;

/**
 * Created by ashak on 10-11-2016.
 */

public interface ProviderItemViewModel {
    void setUpSelection(Provider provider);
    void setFragmentChangeListener(FragmentChangeListener changeListener);
    void onProviderClick(View view);
}
