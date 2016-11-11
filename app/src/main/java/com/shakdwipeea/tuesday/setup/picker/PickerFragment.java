package com.shakdwipeea.tuesday.setup.picker;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shakdwipeea.tuesday.R;
import com.shakdwipeea.tuesday.data.entities.Provider;
import com.shakdwipeea.tuesday.data.entities.ProviderDetails;
import com.shakdwipeea.tuesday.databinding.FragmentAccountPickerBinding;
import com.shakdwipeea.tuesday.data.SelectedProviders;
import com.shakdwipeea.tuesday.setup.ProviderAdapter;
import com.shakdwipeea.tuesday.setup.details.ProviderDetailsActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment for selecting 3rd party account providers
 */
public class PickerFragment extends Fragment {

    FragmentAccountPickerBinding binding;
    Context context;

    ProviderAdapter providerAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_account_picker, container,
                false);

        context = container.getContext();

        //set up providers list
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 4);
        binding.providerList.setLayoutManager(gridLayoutManager);

        providerAdapter = new ProviderAdapter(getProviders());
        binding.providerList.setAdapter(providerAdapter);

        binding.nextButton.setOnClickListener(view -> openProviderDetailsActivity());

        return binding.getRoot();
    }

    private void openProviderDetailsActivity() {
        List<Provider> providers = providerAdapter.getProviders();
        ArrayList<Provider> selectedProviders = new ArrayList<>();

        for (Provider p : providers) {
            if (p.isSelected()) selectedProviders.add(p);
        }

        SelectedProviders
                .setProviderList(selectedProviders);

        Intent intent = new Intent(context, ProviderDetailsActivity.class);
        startActivity(intent);
    }

    // TODO: 08-11-2016 may be get this from server
    public List<Provider> getProviders() {
        int tintColor = ContextCompat.getColor(context, R.color.tintBackground);

        List<Provider> providers = new ArrayList<>();

        Provider provider = new Provider();
        ProviderDetails providerDetails = new ProviderDetails();

        provider.setName("Behance");
        provider.setIcon(ContextCompat.getDrawable(context, R.drawable.behance_color));
        providerDetails.setType(Provider.Type.USERNAME_NO_VERIFICATION);
        provider.setProviderDetails(providerDetails);
        providers.add(provider);

        provider = new Provider();
        providerDetails = new ProviderDetails();
        provider.setName("Blackberry");
        provider.setIcon(ContextCompat.getDrawable(context, R.drawable.blackberry_color_1));
        providerDetails.setType(Provider.Type.USERNAME_NO_VERIFICATION);
        provider.setProviderDetails(providerDetails);
        providers.add(provider);

        provider = new Provider();
        providerDetails = new ProviderDetails();
        provider.setName("Blogger");
        provider.setIcon(ContextCompat.getDrawable(context, R.drawable.blogger_color));
        providerDetails.setType(Provider.Type.USERNAME_NO_VERIFICATION);
        provider.setProviderDetails(providerDetails);
        providers.add(provider);

        provider = new Provider();
        providerDetails = new ProviderDetails();
        provider.setName("Codepen");
        provider.setIcon(ContextCompat.getDrawable(context, R.drawable.codepen_color));
        providerDetails.setType(Provider.Type.USERNAME_NO_VERIFICATION);
        provider.setProviderDetails(providerDetails);
        providers.add(provider);

        provider = new Provider();
        providerDetails = new ProviderDetails();
        provider.setName("Dribble");
        provider.setIcon(ContextCompat.getDrawable(context, R.drawable.dribbble_color));
        providerDetails.setType(Provider.Type.USERNAME_NO_VERIFICATION);
        provider.setProviderDetails(providerDetails);
        providers.add(provider);

        provider = new Provider();
        providerDetails = new ProviderDetails();
        provider.setName("Drive");
        provider.setIcon(ContextCompat.getDrawable(context, R.drawable.drive_color_1));
        providerDetails.setType(Provider.Type.USERNAME_NO_VERIFICATION);
        provider.setProviderDetails(providerDetails);
        providers.add(provider);

        provider = new Provider();
        providerDetails = new ProviderDetails();
        provider.setName("Dropbox");
        provider.setIcon(ContextCompat.getDrawable(context, R.drawable.dropbox_color));
        providerDetails.setType(Provider.Type.USERNAME_NO_VERIFICATION);
        provider.setProviderDetails(providerDetails);
        providers.add(provider);

        provider = new Provider();
        providerDetails = new ProviderDetails();
        provider.setName("Facebook");
        provider.setIcon(ContextCompat.getDrawable(context, R.drawable.facebook_color));
        providerDetails.setType(Provider.Type.USERNAME_NO_VERIFICATION);
        provider.setProviderDetails(providerDetails);
        providers.add(provider);

        provider = new Provider();
        providerDetails = new ProviderDetails();
        provider.setName("Flickr");
        provider.setIcon(ContextCompat.getDrawable(context, R.drawable.flickr_color));
        providerDetails.setType(Provider.Type.USERNAME_NO_VERIFICATION);
        provider.setProviderDetails(providerDetails);
        providers.add(provider);

        provider = new Provider();
        providerDetails = new ProviderDetails();
        provider.setName("Call");
        provider.setIcon(ContextCompat.getDrawable(context, R.drawable.ic_call_black_24dp));
        provider.getIcon().setColorFilter(tintColor, PorterDuff.Mode.SRC_IN);
        providerDetails.setType(Provider.Type.PHONE_NUMBER_VERIFICATION);
        provider.setSelected(true);
        provider.setProviderDetails(providerDetails);
        providers.add(provider);

        return providers;
    }
}
