package com.shakdwipeea.tuesday.setup;

import android.animation.ObjectAnimator;
import android.view.View;
import android.widget.ImageView;

import com.shakdwipeea.tuesday.data.entities.Provider;
import com.shakdwipeea.tuesday.databinding.ProviderPickerItemBinding;

/**
 * Created by ashak on 08-11-2016.
 */

public class ProviderItemViewModel {
    private ProviderPickerItemBinding binding;
    private Provider provider;

    public ProviderItemViewModel(ProviderPickerItemBinding binding, Provider provider) {
        this.binding = binding;
        this.provider = provider;
    }

    public void onProviderSelected() {
        toggleVisibility(binding.selectedIcon);
    }

    private void toggleVisibility(ImageView imageView) {
        if (imageView.getVisibility() == View.VISIBLE) {
            imageView.setVisibility(View.GONE);
        } else {
            imageView.setVisibility(View.VISIBLE);
        }
    }
}
