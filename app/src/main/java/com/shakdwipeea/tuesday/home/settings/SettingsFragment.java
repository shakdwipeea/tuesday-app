package com.shakdwipeea.tuesday.home.settings;


import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shakdwipeea.tuesday.R;
import com.shakdwipeea.tuesday.auth.AuthActivity;
import com.shakdwipeea.tuesday.data.entities.User;
import com.shakdwipeea.tuesday.databinding.FragmentSettingsBinding;
import com.shakdwipeea.tuesday.home.home.ContactItemActionHandler;
import com.shakdwipeea.tuesday.util.Util;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment implements SettingsContract.View {
    private static final String TAG = "SettingsFragment";

    FragmentSettingsBinding binding;
    SettingsPresenter presenter;

    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false);

        presenter = new SettingsPresenter(this, getContext());
        binding.setPresenter(presenter);
        binding.contactItem.setActionHandler(new ContactItemActionHandler());

        presenter.getUser();

        return binding.getRoot();
    }

    @Override
    public void displayError(String reason) {
        Snackbar.make(binding.getRoot(), reason, Snackbar.LENGTH_SHORT)
                .show();
    }

    @Override
    public void displayUserDetails(User user) {
        binding.contactItem.setContact(user);
        Util.displayProfilePic(getContext(), binding.contactItem.profilePic,
                binding.contactItem.placeholderProfilePic, user);
    }

    @Override
    public void showProgress(boolean enable) {
        Log.d(TAG, "showProgress: " + enable);
    }

    @Override
    public void launchAuth() {
        Intent intent = new Intent(getContext(), AuthActivity.class);
        startActivity(intent);
        getActivity().finish();
    }
}
