package com.shakdwipeea.tuesday.setup.picker;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shakdwipeea.tuesday.R;
import com.shakdwipeea.tuesday.data.entities.Provider;
import com.shakdwipeea.tuesday.data.providers.ProviderService;
import com.shakdwipeea.tuesday.data.providers.SelectedProviders;
import com.shakdwipeea.tuesday.databinding.FragmentAccountPickerBinding;
import com.shakdwipeea.tuesday.setup.ProviderAdapter;
import com.shakdwipeea.tuesday.setup.details.ProviderDetailsActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rx.Observable;

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

        getProviderList();

        providerAdapter = new ProviderAdapter();
        binding.providerList.setAdapter(providerAdapter);

        binding.nextButton.setOnClickListener(view -> openProviderDetailsActivity());

        return binding.getRoot();
    }

    private void getProviderList() {
        ProviderService providerService = ProviderService.getInstance();

        HashMap<String, Provider> providerHashMap = providerService.getProviderHashMap();

        SelectedProviders.getInstance()
                .getProviderList()
                .doOnNext(providers -> {
                    // if the provider is selected in preferences then set selected
                    for (Provider p: providers) {
                        Provider provider = providerHashMap.get(p.getName());
                        provider.setSelected(true);
                    }

                    providerAdapter.setProviders(providerService.getProviderList());
                })
                .subscribe();
    }

    private void openProviderDetailsActivity() {
        List<Provider> providers = providerAdapter.getProviders();
        ArrayList<Provider> selectedProviders = new ArrayList<>();

        for (Provider p : providers) {
            if (p.isSelected()) selectedProviders.add(p);
        }

        SelectedProviders
                .getInstance()
                .setProviderList(Observable.from(selectedProviders).toList());

        Intent intent = new Intent(context, ProviderDetailsActivity.class);
        startActivity(intent);
    }}
