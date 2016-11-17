package com.shakdwipeea.tuesday.setup.details;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;

import com.shakdwipeea.tuesday.R;
import com.shakdwipeea.tuesday.data.Preferences;
import com.shakdwipeea.tuesday.data.entities.Provider;
import com.shakdwipeea.tuesday.data.providers.SelectedProviders;
import com.shakdwipeea.tuesday.databinding.ActivityProviderDetailsBinding;
import com.shakdwipeea.tuesday.home.HomeActivity;
import com.shakdwipeea.tuesday.setup.FragmentChangeListener;
import com.shakdwipeea.tuesday.setup.ProviderAdapter;

import org.parceler.Parcels;

public class ProviderDetailsActivity extends AppCompatActivity implements FragmentChangeListener {
    private static final String TAG = "ProviderDetailsActivity";

    private ActivityProviderDetailsBinding binding;

    ProviderAdapter providerAdapter;

    int selectedProviderIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_provider_details);
        setSupportActionBar(binding.toolbar);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        binding.selectedProviderList.setLayoutManager(layoutManager);

        providerAdapter = new ProviderAdapter();
        providerAdapter.setChangeListener(this);
        binding.selectedProviderList.setAdapter(providerAdapter);

        setupProviders();
    }

    private void setupProviders() {
        SelectedProviders.getInstance()
                .getProviderList()
                .doOnNext(providers -> {
                    // TODO: 16-11-2016 edit icon should be there instead of this
                    for (Provider p: providers) {
                        p.setSelected(false);
                    }
                    providerAdapter.setProviders(providers);

                    // Load the first provider initially
                    loadFragment();
                })
                .subscribe();
    }

    private void displayError(String reason) {
        Snackbar.make(binding.getRoot(), reason, Snackbar.LENGTH_SHORT)
                .show();
    }

    @Override
    public void loadFragment() {

        if (selectedProviderIndex < providerAdapter.getProviders().size()) {
            ProviderDetailsFragment fragment = new ProviderDetailsFragment();

            Bundle bundle = new Bundle();
            bundle.putParcelable(ProviderDetailsFragment.SELECTED_PROVIDER_KEY,
                    Parcels.wrap(providerAdapter.getProvider(selectedProviderIndex)));
            fragment.setArguments(bundle);

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_container, fragment);
            ft.commit();

            selectedProviderIndex++;
        } else {
            // All provider's details has been entered and setup is now complete
            Preferences.getInstance(this)
                    .setSetupComplete(true);

            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            finish();
        }
    }

}
