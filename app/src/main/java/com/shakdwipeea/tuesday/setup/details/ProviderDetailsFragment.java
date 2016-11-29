package com.shakdwipeea.tuesday.setup.details;

import android.content.Context;
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
import com.shakdwipeea.tuesday.data.entities.Provider;
import com.shakdwipeea.tuesday.setup.FragmentChangeListener;

import org.parceler.Parcels;

import static com.twitter.sdk.android.core.TwitterCore.TAG;

public class ProviderDetailsFragment extends Fragment implements ProviderDetailsContract.View {
    public static String SELECTED_PROVIDER_KEY = "provider_index";

    ViewDataBinding binding;

    ProviderDetailsPresenter presenter;

    FragmentChangeListener fragChange;
    int selectedProviderIndex;

    Context context;

    Provider provider;

    public ProviderDetailsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = inflater.getContext();

        // Get provider specified
        provider = Parcels.unwrap(getArguments().getParcelable(SELECTED_PROVIDER_KEY));

        // initialize presenter
        presenter = new ProviderDetailsPresenter(this, provider);

        fragChange = (FragmentChangeListener) getActivity();

        // Default value
        int layoutId;

        if (provider.getProviderDetails() != null &&
                provider.getProviderDetails().getType() != null) {

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
        binding.setVariable(BR.actionText, "Save");

        return binding.getRoot();
    }

    public void displayError(String reason) {
        Snackbar.make(binding.getRoot(), reason, Snackbar.LENGTH_SHORT)
                .show();
    }

    @Override
    public void changeButtonText(String text) {
        // TODO: 11-11-2016 achieve this by 2 way binding
        binding.setVariable(BR.actionText, text);
    }

    @Override
    public void loadNextProvider() {
        fragChange.loadFragment(provider);
    }
}
