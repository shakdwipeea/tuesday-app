package com.shakdwipeea.tuesday.setup.details;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.shakdwipeea.tuesday.R;
import com.shakdwipeea.tuesday.data.entities.Provider;
import com.shakdwipeea.tuesday.databinding.ActivityProviderDetailsBinding;
import com.shakdwipeea.tuesday.setup.SelectedProviders;

import java.util.List;

public class ProviderDetailsActivity extends AppCompatActivity {
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
            Log.d(TAG, "onCreate: " + p);
        }
    }

    private void displayError(String reason) {
        Snackbar.make(binding.getRoot(), reason, Snackbar.LENGTH_SHORT)
                .show();
    }

}
