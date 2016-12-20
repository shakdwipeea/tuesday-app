package com.shakdwipeea.tuesday.auth.details;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.shakdwipeea.tuesday.R;
import com.shakdwipeea.tuesday.data.Preferences;
import com.shakdwipeea.tuesday.data.entities.user.User;
import com.shakdwipeea.tuesday.databinding.FragmentDetailsBinding;
import com.shakdwipeea.tuesday.util.Util;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailsFragment extends Fragment implements DetailsContract.View {
    public static final String KEY_TOKEN = "token";
    public static final String KEY_PHONE = "phone";

    FragmentDetailsBinding binding;

    DetailsContract.Presenter presenter;

    MaterialDialog dialog;

    public DetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_details,
                container, false);

        presenter = new DetailsPresenter(this);

        String token = getArguments().getString(KEY_TOKEN);
        String phone = getArguments().getString(KEY_PHONE);

        binding.saveButton.setOnClickListener(v -> {
            User user = new User();
            user.name = binding.nameInput.getText().toString();
            user.token = token;
            user.phoneNumber = phone;
            presenter.saveDetails(user);
        });

        return binding.getRoot();
    }

    @Override
    public void displayProgressBar(Boolean enable) {
        if (dialog == null)
            dialog = Util.createProgressDialog(getContext()).show();

        if (enable)
            dialog.show();
        else
            dialog.dismiss();
    }

    @Override
    public void displayError(String reason) {
        Snackbar.make(binding.getRoot(), reason, Snackbar.LENGTH_SHORT)
                .show();
    }

    @Override
    public void saveUserDetails(User user) {
        Preferences.getInstance(getContext())
                .setUserDetails(user);
    }
}
