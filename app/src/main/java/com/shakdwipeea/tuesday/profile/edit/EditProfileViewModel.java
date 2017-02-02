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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import rx.Observable;

/**
 * Created by akash on 28/1/17.
 */

public class EditProfileViewModel {
    private Context context;
    private SingleViewAdapter<
            Provider,
            EditProfileItemViewModel,
            ProviderDetailEditBinding> socialAdapter, phoneAdapter, emailAdapter;

    public String emailProvider = ProviderNames.Email;
    public String callProvider = ProviderNames.Call;

    public EditProfileViewModel(
            Context context,
            SingleViewAdapter<Provider,
                    EditProfileItemViewModel, ProviderDetailEditBinding> socialAdapter,
            SingleViewAdapter<Provider,
                    EditProfileItemViewModel, ProviderDetailEditBinding> emailAdapter,
            SingleViewAdapter<Provider,
                    EditProfileItemViewModel, ProviderDetailEditBinding> phoneAdapter) {
        this.context = context;
        this.phoneAdapter = phoneAdapter;
        this.emailAdapter = emailAdapter;
        this.socialAdapter = socialAdapter;
    }

    /**
     * Get providers which have not already been added
     * @param addedProviders Providers which have been already added
     * @return List of Provider Names which have not been added
     */
    private List<String> newProviderList(List<Provider> addedProviders) {
        ArrayList<String> providerNames = new ArrayList<>(Arrays.asList(ProviderNames.getAll()));

        for (Provider provider :
                addedProviders) {
            providerNames.remove(provider.getName());
        }

        providerNames.remove(ProviderNames.Call);
        providerNames.remove(ProviderNames.Email);

        return providerNames;
    }

    public void addProviderAccount() {
        new MaterialDialog.Builder(context)
                .title(R.string.select_provider_label)
                .items(newProviderList(socialAdapter.getItemList()))
                .itemsCallbackSingleChoice(-1, (dialog, itemView, which, text) -> {
                    if (text != null) {
                        addAccount(text.toString());
                        return true;
                    }

                    return false;
                })
                .positiveText(R.string.choose)
                .show();
    }

    public void addAccount(String providerName) {
        Provider provider = ProviderService.getInstance()
                .getProviderHashMap().get(providerName);

        switch (providerName) {
            case ProviderNames.Call:
                phoneAdapter.addItem(provider);
                break;

            case ProviderNames.Email:
                emailAdapter.addItem(provider);
                break;

            default:
                socialAdapter.addItem(provider);
        }
    }
}
