package com.shakdwipeea.tuesday.setup.picker;

import android.view.View;

import com.shakdwipeea.tuesday.data.entities.Provider;
import com.shakdwipeea.tuesday.setup.FragmentChangeListener;
import com.shakdwipeea.tuesday.setup.ProviderItemViewModel;

/**
 * Created by ashak on 08-11-2016.
 */

public class SelectProviderViewModel implements ProviderItemViewModel {
    private Provider provider;

    public Provider getProvider() {
        return provider;
    }

    @Override
    public void setUpSelection(Provider provider) {
        this.provider = provider;
    }

    @Override
    public void setFragmentChangeListener(FragmentChangeListener changeListener) {}

    @Override
    public void onProviderClick(View view) {
        if (provider != null) {
            // toggle selection
            provider.setSelected(!provider.isSelected());
        }
    }
}
