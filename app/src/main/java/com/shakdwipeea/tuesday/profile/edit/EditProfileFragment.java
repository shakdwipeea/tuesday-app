package com.shakdwipeea.tuesday.profile.edit;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.shakdwipeea.tuesday.R;
import com.shakdwipeea.tuesday.data.Preferences;
import com.shakdwipeea.tuesday.data.entities.user.Provider;
import com.shakdwipeea.tuesday.data.entities.user.User;
import com.shakdwipeea.tuesday.databinding.FragmentEditProfileBinding;
import com.shakdwipeea.tuesday.databinding.ProviderDetailEditBinding;
import com.shakdwipeea.tuesday.picture.ProfilePicturePresenter;
import com.shakdwipeea.tuesday.picture.ProfilePictureUtil;
import com.shakdwipeea.tuesday.picture.ProfilePictureView;
import com.shakdwipeea.tuesday.profile.view.ProfileContract;
import com.shakdwipeea.tuesday.profile.view.ProfileViewFragment;
import com.shakdwipeea.tuesday.util.Util;
import com.shakdwipeea.tuesday.util.adapter.SingleViewAdapter;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;

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

    MaterialDialog progressDialog;

    Subscription subscription;

    public EditProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_edit_profile,
                container, false);

//        User user = Parcels.unwrap(getActivity().getIntent()
//                .getParcelableExtra(ProfileViewFragment.USER_EXTRA_KEY));
//        if (user == null) {
//            Log.e(TAG, "onCreateView: user parcel not received ");
//        }

        setHasOptionsMenu(true);

        editProfilePresenter = new EditProfilePresenter(this);

//        displayUser(user);

        setupRecyclerViews();

        setupNameDisplay();

        editProfileViewModel = new EditProfileViewModel(getContext(), providerAdapter);
        binding.setVm(editProfileViewModel);

        profilePictureUtil = new ProfilePictureUtil(new ProfilePicturePresenter(this));
        binding.cameraIcon.setOnClickListener(v -> profilePictureUtil.openImageMenu());

        progressDialog = Util.createProgressDialog(getContext()).build();

        return binding.getRoot();
    }

    private void setupNameDisplay() {
        subscription = RxTextView.textChanges(binding.nameInput)
                .debounce(200, TimeUnit.MILLISECONDS)
                .doOnNext(charSequence -> editProfilePresenter.changeName(charSequence.toString()))
                .subscribe(
                        charSequence -> {
                        },
                        Throwable::printStackTrace
                );
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
        subscription.unsubscribe();
    }

    public void displayUser(User user) {
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
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
            case R.id.action_save_details:
                Preferences preferences = Preferences.getInstance(getContext());
                preferences.setSetupComplete(true);
                getActivity().onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Context getApplicationContext() {
        return getActivity().getApplicationContext();
    }

    @Override
    public void setProgressBar(boolean enable) {
        if (enable) progressDialog.show();
        else progressDialog.dismiss();
    }

    @Override
    public void displayError(String reason) {
        Snackbar.make(binding.getRoot(), reason, Snackbar.LENGTH_SHORT)
                .show();
    }

    /**
     * Receive the result from a previous call to
     * {@link #startActivityForResult(Intent, int)}.  This follows the
     * related Activity API as described there in
     * {@link Activity#onActivityResult(int, int, Intent)}.
     *
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode  The integer result code returned by the child activity
     *                    through its setResult().
     * @param data        An Intent, which can return result data to the caller
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        profilePictureUtil.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void displayProfilePic(String url) {
        if (!TextUtils.isEmpty(url)) {
            Picasso.with(getContext())
                    .load(url)
                    .into(binding.profilePic);
        } else {
            // TODO: 17-11-2016 display text drawable from first letter
        }
    }

    @Override
    public void displayProfilePic(Bitmap image) {
        Util.resizeBitmapTo(image, binding.profilePic.getHeight(), binding.profilePic.getWidth())
                .compose(Util.applyComputationScheduler())
                .doOnNext(bitmap -> binding.profilePic.setImageBitmap(bitmap))
                .subscribe();
    }

    @Override
    public void displayProfilePicFromPath(String photoPath) {
        Util.resizeBitmapTo(photoPath,
                binding.profilePic.getHeight(), binding.profilePic.getWidth())
                .compose(Util.applyComputationScheduler())
                .doOnNext(bitmap -> binding.profilePic.setImageBitmap(bitmap))
                .doOnError(throwable -> displayError(throwable.getMessage()))
                .onErrorResumeNext(Observable.empty())
                .subscribe();
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
}
