package com.shakdwipeea.tuesday.profile.edit;

import android.content.Context;
import android.util.Log;

import com.afollestad.materialdialogs.MaterialDialog;
import com.shakdwipeea.tuesday.R;
import com.shakdwipeea.tuesday.data.entities.user.Provider;
import com.shakdwipeea.tuesday.data.entities.user.ProviderDetails;
import com.shakdwipeea.tuesday.data.providers.ProviderNames;
import com.shakdwipeea.tuesday.data.providers.ProviderService;
import com.shakdwipeea.tuesday.databinding.ProviderDetailEditBinding;
import com.shakdwipeea.tuesday.setup.details.ProviderDetailItemViewModel;
import com.shakdwipeea.tuesday.util.adapter.SingleViewAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import rx.Observable;

/**
 * Created by akash on 28/1/17.
 */

public class EditProfileViewModel {
    private static final String TAG = "EditProfileViewModel";

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

    public void addPhoneOrEmailAccount(String providerName) {
        Log.d(TAG, "addPhoneOrEmailAccount: Providername " + providerName);
        if (providerName.equals(ProviderNames.Call))
            showDialog(ProviderNames.Call, getRemainingDetailTypes(phoneAdapter));
        else if (providerName.equals(ProviderNames.Email))
            showDialog(ProviderNames.Email, getRemainingDetailTypes(emailAdapter));
    }

    private void showDialog(String providerName, List<String> remainingDetailTypes) {
        new MaterialDialog.Builder(context)
                .title("Choose a detail type")
                .items(remainingDetailTypes)
                .itemsCallbackSingleChoice(-1, (dialog, itemView, which, text) -> {
                    if (text != null && remainingDetailTypes.size() > 0) {
                        addAccount(providerName, text.toString());
                        return true;
                    }

                    return false;
                })
                .positiveText(R.string.choose)
                .show();
    }

    private List<String> getRemainingDetailTypes(SingleViewAdapter<Provider,
            EditProfileItemViewModel, ProviderDetailEditBinding> adapter) {
        List<Provider> itemList = adapter.getItemList();

        List<String> detailTypes = ProviderDetails.DetailType.getDetailTypes();

        for (Provider p: itemList) {
            detailTypes.remove(p.providerDetails.detailType);
        }

        return detailTypes;
    }

    private void addAccount(String providerName) {
        Log.d(TAG, "addAccount: Provider Name is " + providerName);

        Provider provider = ProviderService.getInstance()
                .getProviderHashMap().get(providerName);

        Provider pToAdd = new Provider(provider);
        Log.d(TAG, "addAccount: pToAdd " + pToAdd);
        switch (providerName) {
            case ProviderNames.Call:
                phoneAdapter.addItem(pToAdd);
                break;

            case ProviderNames.Email:
                emailAdapter.addItem(pToAdd);
                break;

            default:
                socialAdapter.addItem(pToAdd);
        }
    }

    private void addAccount(String providerName, String detailType) {
        Provider provider = ProviderService.getInstance()
                .getProviderHashMap().get(providerName);

        Provider pToAdd = new Provider(provider);
        pToAdd.providerDetails.detailType = detailType;
        Log.d(TAG, "addAccount: pToAdd " + pToAdd);
        switch (providerName) {
            case ProviderNames.Call:
                phoneAdapter.addItem(pToAdd);
                break;

            case ProviderNames.Email:
                emailAdapter.addItem(pToAdd);
                break;

            default:
                socialAdapter.addItem(pToAdd);
        }
    }
}
