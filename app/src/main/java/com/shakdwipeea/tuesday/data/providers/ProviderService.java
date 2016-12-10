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

        provider.setName("Behance");
        provider.setIcon(R.drawable.behance_color);
        providerDetails.setType(Provider.Type.USERNAME_NO_VERIFICATION);
        provider.setProviderDetails(providerDetails);
        providers.add(provider);

        provider = new Provider();
        providerDetails = new ProviderDetails();
        provider.setName("Blackberry");
        provider.setIcon(R.drawable.blackberry_color_1);
        providerDetails.setType(Provider.Type.USERNAME_NO_VERIFICATION);
        provider.setProviderDetails(providerDetails);
        providers.add(provider);

        provider = new Provider();
        providerDetails = new ProviderDetails();
        provider.setName("Blogger");
        provider.setIcon(R.drawable.blogger_color);
        providerDetails.setType(Provider.Type.USERNAME_NO_VERIFICATION);
        provider.setProviderDetails(providerDetails);
        providers.add(provider);

        provider = new Provider();
        providerDetails = new ProviderDetails();
        provider.setName("Codepen");
        provider.setIcon(R.drawable.codepen_color);
        providerDetails.setType(Provider.Type.USERNAME_NO_VERIFICATION);
        provider.setProviderDetails(providerDetails);
        providers.add(provider);

        provider = new Provider();
        providerDetails = new ProviderDetails();
        provider.setName("Dribble");
        provider.setIcon(R.drawable.dribbble_color);
        providerDetails.setType(Provider.Type.USERNAME_NO_VERIFICATION);
        provider.setProviderDetails(providerDetails);
        providers.add(provider);

        provider = new Provider();
        providerDetails = new ProviderDetails();
        provider.setName("Drive");
        provider.setIcon(R.drawable.drive_color_1);
        providerDetails.setType(Provider.Type.USERNAME_NO_VERIFICATION);
        provider.setProviderDetails(providerDetails);
        providers.add(provider);

        provider = new Provider();
        providerDetails = new ProviderDetails();
        provider.setName("Dropbox");
        provider.setIcon(R.drawable.dropbox_color);
        providerDetails.setType(Provider.Type.USERNAME_NO_VERIFICATION);
        provider.setProviderDetails(providerDetails);
        providers.add(provider);

        provider = new Provider();
        providerDetails = new ProviderDetails();
        provider.setName("Facebook");
        provider.setIcon(R.drawable.facebook_color);
        providerDetails.setType(Provider.Type.USERNAME_NO_VERIFICATION);
        provider.setProviderDetails(providerDetails);
        providers.add(provider);

        provider = new Provider();
        providerDetails = new ProviderDetails();
        provider.setName("Flickr");
        provider.setIcon(R.drawable.flickr_color);
        providerDetails.setType(Provider.Type.USERNAME_NO_VERIFICATION);
        provider.setProviderDetails(providerDetails);
        providers.add(provider);

        provider = new Provider();
        providerDetails = new ProviderDetails();
        provider.setName("Call");
        provider.setIcon(R.drawable.ic_call_black_24dp);
        providerDetails.setType(Provider.Type.PHONE_NUMBER_VERIFICATION);
        provider.setSelected(true);
        provider.setProviderDetails(providerDetails);
        providers.add(provider);

        return providers;
    }

}
