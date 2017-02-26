package com.shakdwipeea.tuesday.home.settings;


import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.crash.FirebaseCrash;
import com.shakdwipeea.tuesday.R;
import com.shakdwipeea.tuesday.auth.AuthActivity;
import com.shakdwipeea.tuesday.data.Preferences;
import com.shakdwipeea.tuesday.data.contacts.sync.SyncUtils;
import com.shakdwipeea.tuesday.data.entities.user.User;
import com.shakdwipeea.tuesday.databinding.FragmentSettingsBinding;
import com.shakdwipeea.tuesday.home.home.ContactItemActionHandler;
import com.shakdwipeea.tuesday.util.Util;
import com.shakdwipeea.tuesday.util.perm.PermViewUtil;
import com.shakdwipeea.tuesday.util.perm.RequestPermissionInterface;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment
        implements SettingsContract.View, RequestPermissionInterface {
    private static final String TAG = "SettingsFragment";

    FragmentSettingsBinding binding;
    SettingsPresenter presenter;

    Preferences preferences;
    PermViewUtil permViewUtil;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        presenter.unSubscribe();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false);

        preferences = Preferences.getInstance(getContext());

        presenter = new SettingsPresenter(this, getContext());
        binding.setPresenter(presenter);
        binding.contactItem.setActionHandler(new ContactItemActionHandler());

        binding.syncContactSwitch.setChecked(preferences.isSync());
        binding.syncContactSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            preferences.setSync(isChecked);

            if (!isChecked) return;

            permViewUtil = new PermViewUtil(binding.getRoot());
            permViewUtil.performActionWithPermissions(
                    getContext(),
                    Manifest.permission.GET_ACCOUNTS,
                    new String[]{Manifest.permission.GET_ACCOUNTS},
                    this,
                    () -> {
                        try {
                            Account[] accounts = AccountManager.get(getContext())
                                    .getAccountsByType(SyncUtils.ACCOUNT_TYPE);

                            for (Account account : accounts) {
                                if (account.name.equals(preferences.getAccountName())) {
                                    SyncUtils.TriggerRefresh(account);
                                    return;
                                }
                            }

                            Log.e(TAG, "onCreateView: Could not find account to sync with ");
                        } catch (Exception exception) {
                            Log.e(TAG, "onCreateView: Error ", exception);
                            displayError("Contact Sync will not work. Please retry.");
                            FirebaseCrash.report(exception);
                        }
                    }
            );
        });

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
