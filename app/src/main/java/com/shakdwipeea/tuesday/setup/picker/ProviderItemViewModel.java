package com.shakdwipeea.tuesday.setup.picker;

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
        // toggle selection
        provider.setSelected(!provider.isSelected());
    }
}
