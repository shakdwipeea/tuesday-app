package com.shakdwipeea.tuesday.setup.picker;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.shakdwipeea.tuesday.data.entities.user.Provider;
import com.shakdwipeea.tuesday.setup.FragmentChangeListener;
import com.shakdwipeea.tuesday.setup.ProviderItemViewModel;

/**
 * Created by ashak on 08-11-2016.
 */

public class SelectProviderViewModel implements ProviderItemViewModel {
    private Provider provider;
    private Context context;

    public SelectProviderViewModel(Context context) {
        this.context = context;
    }

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

    @Override
    public Drawable getDrawableFrom(int resId) {
        return ContextCompat.getDrawable(context, resId);
    }
}
