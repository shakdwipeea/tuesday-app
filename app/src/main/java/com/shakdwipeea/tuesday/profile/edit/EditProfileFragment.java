package com.shakdwipeea.tuesday.profile.edit;


import android.app.Activity;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.shakdwipeea.tuesday.R;
import com.shakdwipeea.tuesday.data.entities.user.Provider;
import com.shakdwipeea.tuesday.data.entities.user.User;
import com.shakdwipeea.tuesday.databinding.FragmentEditProfileBinding;
import com.shakdwipeea.tuesday.databinding.ProviderDetailEditBinding;
import com.shakdwipeea.tuesday.picture.ProfilePicturePresenter;
import com.shakdwipeea.tuesday.picture.ProfilePictureUtil;
import com.shakdwipeea.tuesday.picture.ProfilePictureView;
import com.shakdwipeea.tuesday.profile.view.ProfileContract;
import com.shakdwipeea.tuesday.profile.view.ProfileViewFragment;
import com.shakdwipeea.tuesday.util.adapter.SingleViewAdapter;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditProfileFragment extends Fragment
        implements EditProfileContract.View, ProfilePictureView {
    private static final String TAG = "EditProfileFragment";

    FragmentEditProfileBinding binding;

    SingleViewAdapter<
            Provider,
            EditProfileItemViewModel,
            ProviderDetailEditBinding> phoneAdapter, mailAdapter, providerAdapter;

    EditProfilePresenter editProfilePresenter;
    EditProfileViewModel editProfileViewModel;

    ProfilePictureUtil profilePictureUtil;

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

        setHasOptionsMenu(true);

        editProfilePresenter = new EditProfilePresenter(this);

        profilePictureUtil = new ProfilePictureUtil(new ProfilePicturePresenter(this), this);

        displayUser(user);

        setupRecyclerViews();

        editProfileViewModel = new EditProfileViewModel(getContext(), providerAdapter);
        binding.setVm(editProfileViewModel);

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
        phoneAdapter = new SingleViewAdapter<>(R.layout.provider_detail_edit,
                (provider) -> new EditProfileItemViewModel(editProfilePresenter, provider));

        binding.callDetailList.setLayoutManager(phoneLM);
        binding.callDetailList.setAdapter(phoneAdapter);

        LinearLayoutManager mailLM = new LinearLayoutManager(getContext());
        mailAdapter = new SingleViewAdapter<>(R.layout.provider_detail_edit,
                (provider) -> new EditProfileItemViewModel(editProfilePresenter, provider));

        binding.emailDetailList.setLayoutManager(mailLM);
        binding.emailDetailList.setAdapter(mailAdapter);

        LinearLayoutManager providerLM = new LinearLayoutManager(getContext());
        providerAdapter = new SingleViewAdapter<>(R.layout.provider_detail_edit,
                (provider) -> new EditProfileItemViewModel(editProfilePresenter, provider));

        binding.providerList.setLayoutManager(providerLM);
        binding.providerList.setAdapter(providerAdapter);

    }

    /**
     * Initialize the contents of the Fragment host's standard options menu.  You
     * should place your menu items in to <var>menu</var>.  For this method
     * to be called, you must have first called {@link #setHasOptionsMenu}.  See
     * {@link Activity#onCreateOptionsMenu(Menu) Activity.onCreateOptionsMenu}
     * for more information.
     *
     * @param menu     The options menu in which you place your items.
     * @param inflater
     * @see #setHasOptionsMenu
     * @see #onPrepareOptionsMenu
     * @see #onOptionsItemSelected
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_edit_profile, menu);
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
            case R.id.action_save_details:
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Context getApplicationContext() {
        return null;
    }

    @Override
    public void setProgressBar(boolean enable) {

    }

    @Override
    public void displayProfilePic(String url) {

    }

    @Override
    public void displayError(String reason) {
        Snackbar.make(binding.getRoot(), reason, Snackbar.LENGTH_SHORT)
                .show();
    }

    @Override
    public void displayProfilePicFromPath(String filePath) {

    }

    @Override
    public void displayProfilePic(Bitmap bitmap) {

    }

    @Override
    public void addProvider(Provider provider) {
        providerAdapter.addItem(provider);
    }

    @Override
    public void clearProvider() {
        providerAdapter.clear();
    }

    @Override
    public void addCallDetails(Provider provider) {
        phoneAdapter.addItem(provider);
    }

    @Override
    public void clearCallDetails() {
        phoneAdapter.clear();
    }

    @Override
    public void addMailDetails(Provider provider) {
        mailAdapter.addItem(provider);
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
