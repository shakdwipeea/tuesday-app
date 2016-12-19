package com.shakdwipeea.tuesday.setup.details;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;

import com.shakdwipeea.tuesday.R;
import com.shakdwipeea.tuesday.data.Preferences;
import com.shakdwipeea.tuesday.data.entities.user.Provider;
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
                    providerAdapter.setProviders(providers);

                    // Load the first provider initially
                    loadFragment(providers.get(0));
                })
                .subscribe(
                        providers -> Log.d(TAG, "setupProviders: providers" + providers),
                        Throwable::printStackTrace
                );


        binding.buttonDone.setOnClickListener(view -> {
            // All provider's details has been entered and setup is now complete
            Preferences.getInstance(this)
                    .setSetupComplete(true);

            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void displayError(String reason) {
        Snackbar.make(binding.getRoot(), reason, Snackbar.LENGTH_SHORT)
                .show();
    }

    @Override
    public void loadFragment(Provider curProvider) {
        providerAdapter.unSelectExcept(curProvider);

        ProviderDetailsFragment fragment = new ProviderDetailsFragment();

        Bundle bundle = new Bundle();
        bundle.putParcelable(ProviderDetailsFragment.SELECTED_PROVIDER_KEY,
                Parcels.wrap(curProvider));
        fragment.setArguments(bundle);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, fragment);
        ft.commit();
    }

}
