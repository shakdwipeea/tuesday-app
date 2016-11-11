package com.shakdwipeea.tuesday.setup.details;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;

import com.shakdwipeea.tuesday.R;
import com.shakdwipeea.tuesday.data.SelectedProviders;
import com.shakdwipeea.tuesday.data.entities.Provider;
import com.shakdwipeea.tuesday.databinding.ActivityProviderDetailsBinding;
import com.shakdwipeea.tuesday.setup.FragmentChangeListener;
import com.shakdwipeea.tuesday.setup.ProviderAdapter;

import java.util.List;

public class ProviderDetailsActivity extends AppCompatActivity implements FragmentChangeListener {
    private static final String TAG = "ProviderDetailsActivity";

    private List<Provider> providerList;
    private ActivityProviderDetailsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_provider_details);
        setSupportActionBar(binding.toolbar);

        if (SelectedProviders.getProviderList() == null) {
            displayError("Providers not given");
            return;
        }

        providerList = SelectedProviders.getProviderList();
        for (Provider p: providerList) {
            p.setSelected(false);
            Log.d(TAG, "onCreate: " + p);
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        binding.selectedProviderList.setLayoutManager(layoutManager);

        ProviderAdapter providerAdapter = new ProviderAdapter(providerList);
        providerAdapter.setChangeListener(this);
        binding.selectedProviderList.setAdapter(providerAdapter);
    }

    private void displayError(String reason) {
        Snackbar.make(binding.getRoot(), reason, Snackbar.LENGTH_SHORT)
                .show();
    }

    @Override
    public void loadFragment(Provider provider) {
        ProviderDetailsFragment fragment = new ProviderDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ProviderDetailsFragment.SELECTED_PROVIDER_INDEX
                , providerList.indexOf(provider));
        fragment.setArguments(bundle);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, fragment);
        ft.commit();
    }

}
