package com.shakdwipeea.tuesday.profile.edit;

import android.content.Context;
import android.util.Log;

import com.afollestad.materialdialogs.MaterialDialog;
import com.shakdwipeea.tuesday.R;
import com.shakdwipeea.tuesday.data.entities.user.Provider;
import com.shakdwipeea.tuesday.data.entities.user.ProviderDetails;
import com.shakdwipeea.tuesday.data.providers.ProviderNames;
import com.shakdwipeea.tuesday.data.providers.ProviderService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by akash on 28/1/17.
 */

public class EditProfileViewModel {
    private static final String TAG = "EditProfileViewModel";

    private Context context;
    private EditProfileAdapter socialAdapter;
    private EditProfilePresenter editProfilePresenter;

    public EditProfileViewModel(
            Context context,
            EditProfileAdapter socialAdapter, EditProfilePresenter editProfilePresenter) {
        this.context = context;
        this.socialAdapter = socialAdapter;
        this.editProfilePresenter = editProfilePresenter;
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
                .items(newProviderList(socialAdapter.getSocialList()))
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
        // todo get separate list for phone and email
        rx.Observable.from(socialAdapter.getCallList())
                .filter(provider -> provider.name.equals(providerName))
                .toList()
                .map(this::getRemainingDetailTypes)
                .subscribe(
                        providers -> {
                            if (providerName.equals(ProviderNames.Call))
                                showDialog(ProviderNames.Call, providers);
                            else if (providerName.equals(ProviderNames.Email))
                                showDialog(ProviderNames.Email, providers);
                        }
                );
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

    private rx.Observable<List<Provider>> getProvidersWithName(List<Provider> providers, String providerName) {
        return rx.Observable.from(providers)
                .filter(provider -> provider.name.equals(providerName))
                .toList();
    }

    /**
     * Given a list of providers of same type, it returns which detail type can be used.
     * This is to be used only with Call and Email Providers.
     * For example, if we have Call_Primary and Call_Work in firebase,
     * then when we reach here We will have two providers with same names Call
     * having different detailType. So passing these two providers as itemList
     * will return the missing detailType. i don't remember the name.
     *
     * @param itemList Provider list of same name
     * @return Remaining detailTypes
     */
    private List<String> getRemainingDetailTypes(List<Provider> itemList) {
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
        editProfilePresenter.saveDetails(pToAdd);

    }

    /**
     * used for phone and email in which case the detail type is required
     *
     * @param providerName Provider to change
     * @param detailType Detail type
     */
    private void addAccount(String providerName, String detailType) {
        Provider provider = ProviderService.getInstance()
                .getProviderHashMap().get(providerName);

        Provider pToAdd = new Provider(provider);
        pToAdd.providerDetails.detailType = detailType;

        Log.d(TAG, "addAccount: pToAdd " + pToAdd);
        editProfilePresenter.saveDetails(pToAdd);
    }
}
