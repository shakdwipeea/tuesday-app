package com.shakdwipeea.tuesday.profile.edit;


import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.shakdwipeea.tuesday.R;
import com.shakdwipeea.tuesday.data.entities.user.ProviderDetails;
import com.shakdwipeea.tuesday.data.entities.user.User;
import com.shakdwipeea.tuesday.databinding.FragmentEditProfileBinding;
import com.shakdwipeea.tuesday.databinding.ProviderDetailEditBinding;
import com.shakdwipeea.tuesday.profile.view.ProfileViewFragment;
import com.shakdwipeea.tuesday.util.adapter.SingleViewAdapter;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditProfileFragment extends Fragment implements EditProfileContract.View {
    private static final String TAG = "EditProfileFragment";

    FragmentEditProfileBinding binding;

    SingleViewAdapter<
            ProviderDetails,
            EditProfileItemViewModel,
            ProviderDetailEditBinding> phoneAdapter, mailAdapter, providerAdapter;

    EditProfilePresenter editProfilePresenter;

    public EditProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_edit_profile,
                container, false);

        User user = Parcels.unwrap(getActivity().getIntent()
                .getParcelableExtra(ProfileViewFragment.USER_EXTRA_KEY));
        if (user == null) {
            Log.e(TAG, "onCreateView: user parcel not received ");
        }

        editProfilePresenter = new EditProfilePresenter(this);

        displayUser(user);

        setupRecyclerViews();

        return binding.getRoot();
    }

    /**
     * Called when the fragment is visible to the user and actively running.
     * This is generally
     * tied to {@link Activity#onResume() Activity.onResume} of the containing
     * Activity's lifecycle.
     */
    @Override
    public void onResume() {
        super.onResume();
        editProfilePresenter.subscribe();
    }

    /**
     * Called when the Fragment is no longer resumed.  This is generally
     * tied to {@link Activity#onPause() Activity.onPause} of the containing
     * Activity's lifecycle.
     */
    @Override
    public void onPause() {
        super.onPause();
        editProfilePresenter.unSubscribe();
    }

    private void displayUser(User user) {
        binding.nameInput.setText(user.name);

        if (user.pic != null) {
            Picasso.with(getContext())
                    .load(user.pic)
                    .into(binding.profilePic);
        }
    }

    public void setupRecyclerViews() {
        LinearLayoutManager phoneLM = new LinearLayoutManager(getContext());
        phoneAdapter = new SingleViewAdapter<>(new EditProfileItemViewModel(),
                R.layout.provider_detail_edit);

        binding.callDetailList.setLayoutManager(phoneLM);
        binding.callDetailList.setAdapter(phoneAdapter);

        LinearLayoutManager mailLM = new LinearLayoutManager(getContext());
        mailAdapter =
                new SingleViewAdapter<>(new EditProfileItemViewModel(),
                        R.layout.provider_detail_edit);

        binding.emailDetailList.setLayoutManager(mailLM);
        binding.emailDetailList.setAdapter(mailAdapter);

        LinearLayoutManager providerLM = new LinearLayoutManager(getContext());
        providerAdapter =
                new SingleViewAdapter<>(new EditProfileItemViewModel(),
                        R.layout.provider_detail_edit);

        binding.providerList.setLayoutManager(providerLM);
        binding.providerList.setAdapter(providerAdapter);

    }

    /**
     * This hook is called whenever an item in your options menu is selected.
     * The default implementation simply returns false to have the normal
     * processing happen (calling the item's Runnable or sending a message to
     * its Handler as appropriate).  You can use this method for any items
     * for which you would like to do processing without those other
     * facilities.
     * <p>
     * <p>Derived classes should call through to the base class for it to
     * perform the default menu handling.
     *
     * @param item The menu item that was selected.
     * @return boolean Return false to allow normal menu processing to
     * proceed, true to consume it here.
     * @see #onCreateOptionsMenu
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void displayError(String reason) {
        Snackbar.make(binding.getRoot(), reason, Snackbar.LENGTH_SHORT)
                .show();
    }

    @Override
    public void addProvider(ProviderDetails providerDetails) {
        providerAdapter.addItem(providerDetails);
    }

    @Override
    public void clearProvider() {
        providerAdapter.clear();
    }

    @Override
    public void addCallDetails(ProviderDetails callDetails) {
        phoneAdapter.addItem(callDetails);
    }

    @Override
    public void clearCallDetails() {
        phoneAdapter.clear();
    }

    @Override
    public void addMailDetails(ProviderDetails mailDetails) {
        mailAdapter.addItem(mailDetails);
    }

    @Override
    public void clearMailDetails() {
        mailAdapter.clear();
    }

    @Override
    public void displayProgress(boolean enable) {
        Log.d(TAG, "displayProgress: " + enable);
    }
}
