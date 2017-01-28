package com.shakdwipeea.tuesday.profile.edit;

import android.content.Context;

import com.afollestad.materialdialogs.MaterialDialog;
import com.shakdwipeea.tuesday.R;
import com.shakdwipeea.tuesday.data.entities.user.Provider;
import com.shakdwipeea.tuesday.data.providers.ProviderNames;
import com.shakdwipeea.tuesday.data.providers.ProviderService;
import com.shakdwipeea.tuesday.databinding.ProviderDetailEditBinding;
import com.shakdwipeea.tuesday.setup.details.ProviderDetailItemViewModel;
import com.shakdwipeea.tuesday.util.adapter.SingleViewAdapter;

import java.util.Arrays;

/**
 * Created by akash on 28/1/17.
 */

public class EditProfileViewModel {
    private Context context;
    private SingleViewAdapter<
            Provider,
            EditProfileItemViewModel,
            ProviderDetailEditBinding> adapter;

    public EditProfileViewModel(Context context, SingleViewAdapter<Provider,
            EditProfileItemViewModel, ProviderDetailEditBinding> adapter) {
        this.context = context;
        this.adapter = adapter;
    }

    public void addProviderAccount() {
        new MaterialDialog.Builder(context)
                .title(R.string.select_provider_label)
                .items(Arrays.asList(ProviderNames.getAll()))
                .itemsCallbackSingleChoice(-1, (dialog, itemView, which, text) -> {
                    Provider provider = ProviderService.getInstance()
                            .getProviderHashMap().get(text.toString());
                    adapter.addItem(provider);
                    return true;
                })
                .positiveText(R.string.choose)
                .show();
    }
}
