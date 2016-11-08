package com.shakdwipeea.tuesday.setup;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shakdwipeea.tuesday.R;
import com.shakdwipeea.tuesday.data.entities.Provider;
import com.shakdwipeea.tuesday.databinding.FragmentAccountPickerBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment for selecting 3rd party account providers
 */
public class PickerFragment extends Fragment {

    FragmentAccountPickerBinding binding;
    Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_account_picker, container, false);

        context = container.getContext();

        //set up providers list
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 4);
        binding.providerList.setLayoutManager(gridLayoutManager);

        ProviderAdapter providerAdapter = new ProviderAdapter(getProviders());
        binding.providerList.setAdapter(providerAdapter);

        return binding.getRoot();
    }

    // TODO: 08-11-2016 may be get this from server
    public List<Provider> getProviders() {
        List<Provider> providers = new ArrayList<>();

        Provider provider = new Provider();
        provider.name = "Behance";
        provider.icon = ContextCompat.getDrawable(context, R.drawable.behance_color);
        providers.add(provider);

        provider = new Provider();
        provider.name = "Blackberry";
        provider.icon = ContextCompat.getDrawable(context, R.drawable.blackberry_color_1);
        providers.add(provider);

        provider = new Provider();
        provider.name = "Blogger";
        provider.icon = ContextCompat.getDrawable(context, R.drawable.blogger_color);
        providers.add(provider);

        provider = new Provider();
        provider.name = "Codepen";
        provider.icon = ContextCompat.getDrawable(context, R.drawable.codepen_color);
        providers.add(provider);

        provider = new Provider();
        provider.name = "Dribble";
        provider.icon = ContextCompat.getDrawable(context, R.drawable.dribbble_color);
        providers.add(provider);

        provider = new Provider();
        provider.name = "Drive";
        provider.icon = ContextCompat.getDrawable(context, R.drawable.drive_color_1);
        providers.add(provider);

        provider = new Provider();
        provider.name = "Dropbox";
        provider.icon = ContextCompat.getDrawable(context, R.drawable.dropbox_color);
        providers.add(provider);

        provider = new Provider();
        provider.name = "Facebook";
        provider.icon = ContextCompat.getDrawable(context, R.drawable.facebook_color);
        providers.add(provider);

        provider = new Provider();
        provider.name = "Flickr";
        provider.icon = ContextCompat.getDrawable(context, R.drawable.flickr_color);
        providers.add(provider);


        return providers;
    }
}
