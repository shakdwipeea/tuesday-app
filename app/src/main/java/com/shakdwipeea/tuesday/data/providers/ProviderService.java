package com.shakdwipeea.tuesday.data.providers;

import com.shakdwipeea.tuesday.R;
import com.shakdwipeea.tuesday.data.entities.user.Provider;
import com.shakdwipeea.tuesday.data.entities.user.ProviderDetails;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ashak on 12-11-2016.
 */

public class ProviderService {
    private static ProviderService providerService;

    private List<Provider> providerList;

    private ProviderService() {
        this.providerList = getProviders();
    }

    public static ProviderService getInstance() {
        if (providerService == null)
            providerService = new ProviderService();

        return providerService;
    }

    public List<Provider> getProviderList() {
        return providerList;
    }

    public HashMap<String, Provider> getProviderHashMap() {
        return getMapFromList(this.providerList);
    }

    private HashMap<String, Provider> getMapFromList(List<Provider> providerList) {
        HashMap<String, Provider> providers = new HashMap<>();
        for (Provider p: providerList) {
            providers.put(p.getName(), p);
        }
        return providers;
    }

    private List<Provider> getProviders() {
        // TODO: 12-11-2016 move this somewhere
        //int tintColor = ContextCompat.getColor(context, R.color.tintBackground);

        List<Provider> providers = new ArrayList<>();

        Provider provider = new Provider();
        ProviderDetails providerDetails = new ProviderDetails();


        provider = new Provider();
        providerDetails = new ProviderDetails();
        provider.setName(ProviderNames.Facebook);
        provider.setIcon(R.drawable.facebook_color);
        providerDetails.setType(Provider.Type.API_VERIFICATION);
        provider.setProviderDetails(providerDetails);
        providers.add(provider);

        provider = new Provider();
        providerDetails = new ProviderDetails();
        provider.setName(ProviderNames.Call);
        provider.setIcon(R.drawable.ic_call_black_24dp);
        providerDetails.setType(Provider.Type.PHONE_NUMBER_VERIFICATION);
        provider.setSelected(true);
        provider.setProviderDetails(providerDetails);
        providers.add(provider);

        provider = new Provider();
        providerDetails = new ProviderDetails();
        provider.setName(ProviderNames.Email);
        provider.setIcon(R.drawable.ic_email_black_24dp);
        providerDetails.setType(Provider.Type.USERNAME_NO_VERIFICATION);
        provider.setSelected(true);
        provider.setProviderDetails(providerDetails);
        providers.add(provider);

        provider = new Provider();
        providerDetails = new ProviderDetails();
        provider.setName(ProviderNames.Github);
        provider.setIcon(R.drawable.github_color);
        providerDetails.setType(Provider.Type.USERNAME_NO_VERIFICATION);
        provider.setSelected(true);
        provider.setProviderDetails(providerDetails);
        providers.add(provider);

        provider = new Provider();
        providerDetails = new ProviderDetails();
        provider.setName(ProviderNames.Google);
        provider.setIcon(R.drawable.google_color);
        providerDetails.setType(Provider.Type.USERNAME_NO_VERIFICATION);
        provider.setSelected(true);
        provider.setProviderDetails(providerDetails);
        providers.add(provider);

        provider = new Provider();
        providerDetails = new ProviderDetails();
        provider.setName(ProviderNames.LinkedIn);
        provider.setIcon(R.drawable.linkedin_color);
        providerDetails.setType(Provider.Type.API_VERIFICATION);
        provider.setSelected(true);
        provider.setProviderDetails(providerDetails);
        providers.add(provider);

        provider = new Provider();
        providerDetails = new ProviderDetails();
        provider.setName(ProviderNames.StackOverflow);
        provider.setIcon(R.drawable.stackoverflow_color);
        providerDetails.setType(Provider.Type.USERNAME_NO_VERIFICATION);
        provider.setSelected(true);
        provider.setProviderDetails(providerDetails);
        providers.add(provider);

        provider = new Provider();
        providerDetails = new ProviderDetails();
        provider.setName(ProviderNames.Twitter);
        provider.setIcon(R.drawable.twitter_color);
        providerDetails.setType(Provider.Type.USERNAME_NO_VERIFICATION);
        provider.setSelected(true);
        provider.setProviderDetails(providerDetails);
        providers.add(provider);

        provider = new Provider();
        providerDetails = new ProviderDetails();
        provider.setName(ProviderNames.WhatsApp);
        provider.setIcon(R.drawable.whatsapp_color);
        providerDetails.setType(Provider.Type.PHONE_NUMBER_NO_VERIFICATION);
        provider.setSelected(true);
        provider.setProviderDetails(providerDetails);
        providers.add(provider);

        return providers;
    }

}
