package com.shakdwipeea.tuesday.setup.details;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.databinding.library.baseAdapters.BR;
import com.shakdwipeea.tuesday.R;
import com.shakdwipeea.tuesday.data.SelectedProviders;
import com.shakdwipeea.tuesday.data.entities.Provider;

import static com.twitter.sdk.android.core.TwitterCore.TAG;

/**
 * A placeholder fragment containing a simple view.
 */
public class ProviderDetailsFragment extends Fragment implements ProviderDetailsContract.View {
    public static String SELECTED_PROVIDER_INDEX = "provider_index";

    ViewDataBinding binding;

    ProviderDetailsPresenter presenter;

    public ProviderDetailsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        int selectedProviderIndex = getArguments().getInt(SELECTED_PROVIDER_INDEX);

        Provider provider = SelectedProviders.getProviderList().get(selectedProviderIndex);

        presenter = new ProviderDetailsPresenter(this, provider);

        // Default value
        int layoutId;

        if (provider.getProviderDetails().getType() != null) {
            switch (provider.getProviderDetails().getType()) {
                case PHONE_NUMBER_NO_VERIFICATION:
                case PHONE_NUMBER_VERIFICATION:
                    layoutId = R.layout.fragment_provider_call;
                    break;
                default:
                    layoutId = R.layout.fragment_provider_username;
            }
        } else {
            layoutId = R.layout.fragment_provider_details;
            Log.d(TAG, "onCreateView: Provider type is null for some reason" + provider);
        }

        binding = DataBindingUtil.inflate(inflater, layoutId, container, false);

        binding.setVariable(BR.provider, provider);
        binding.setVariable(BR.presenter, presenter);

        return binding.getRoot();
    }

    public void displayError(String reason) {
        Snackbar.make(binding.getRoot(), reason, Snackbar.LENGTH_SHORT)
                .show();
    }
}
