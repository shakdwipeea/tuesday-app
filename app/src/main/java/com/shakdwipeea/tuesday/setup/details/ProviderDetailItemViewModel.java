package com.shakdwipeea.tuesday.setup.details;

import android.content.Context;
import android.view.View;

import com.shakdwipeea.tuesday.data.entities.Provider;
import com.shakdwipeea.tuesday.setup.FragmentChangeListener;
import com.shakdwipeea.tuesday.setup.picker.SelectProviderViewModel;

/**
 * Created by ashak on 10-11-2016.
 */

public class ProviderDetailItemViewModel extends SelectProviderViewModel {
    private Provider provider;
    FragmentChangeListener changeListener;

    public ProviderDetailItemViewModel(Context context) {
        super(context);
    }

    @Override
    public void setUpSelection(Provider provider) {
        super.setUpSelection(provider);
        this.provider = super.getProvider();
    }

    @Override
    public void setFragmentChangeListener(FragmentChangeListener changeListener) {
        this.changeListener = changeListener;
    }

    @Override
    public void onProviderClick(View view) {
        super.onProviderClick(view);
        if (changeListener != null) changeListener.loadFragment(provider);
    }
}
