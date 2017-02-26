package com.shakdwipeea.tuesday.profile.view;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.shakdwipeea.tuesday.R;
import com.shakdwipeea.tuesday.data.PermConstants;
import com.shakdwipeea.tuesday.data.entities.user.Provider;
import com.shakdwipeea.tuesday.data.entities.user.ProviderDetails;
import com.shakdwipeea.tuesday.data.entities.user.User;
import com.shakdwipeea.tuesday.databinding.CallItemBinding;
import com.shakdwipeea.tuesday.databinding.FragmentProfileViewBinding;
import com.shakdwipeea.tuesday.databinding.MailItemBinding;
import com.shakdwipeea.tuesday.picture.ProfilePictureUtil;
import com.shakdwipeea.tuesday.profile.edit.EditProfileFragment;
import com.shakdwipeea.tuesday.setup.ProviderAdapter;
import com.shakdwipeea.tuesday.util.Util;
import com.shakdwipeea.tuesday.util.adapter.SingleViewAdapter;
import com.shakdwipeea.tuesday.util.perm.PermViewUtil;
import com.shakdwipeea.tuesday.util.perm.RequestPermissionInterface;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.util.List;

import rx.Observable;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileViewFragment extends Fragment 
        implements ProfileContract.View, RequestPermissionInterface{
    
    private static final String TAG = "ProfileViewFragment";

    public static String PROFILE_IMAGE_EXTRA = "profilePic";
    public static String USER_EXTRA_KEY = "user";
    MaterialDialog progressBar;
    User user;
    ProviderAdapter providerAdapter;
    SingleViewAdapter<ProviderDetails, CallItemViewModel, CallItemBinding> callListAdapter;
    SingleViewAdapter<ProviderDetails, MailItemViewModel, MailItemBinding> mailListAdapter;
    ProfilePictureUtil pictureUtil;
    PermViewUtil permViewUtil;
    int colorAcc;
    int white;
    Drawable whiteRect;
    FragmentProfileViewBinding binding;
    private ProfilePresenter presenter;
    private Drawable thumbnailDrawable;

    private Provider curProvider;

    private String loggedInUserId;

    public ProfileViewFragment() {
        // Required empty public constructor
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
        presenter.subscribe(user);
    }

    /**
     * Called when the Fragment is no longer resumed.  This is generally
     * tied to {@link Activity#onPause() Activity.onPause} of the containing
     * Activity's lifecycle.
     */
    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil
                .inflate(inflater, R.layout.fragment_profile_view, container, false);

        colorAcc = ContextCompat.getColor(getContext(), R.color.colorAcc);
        white = ContextCompat.getColor(getContext(), android.R.color.white);
        whiteRect = ContextCompat.getDrawable(getContext(), R.drawable.rectangle_rounded);

        setHasOptionsMenu(true);

        user = Parcels.unwrap(getActivity().getIntent().getParcelableExtra(USER_EXTRA_KEY));
        if (user == null) {
            displayError("User not provided");
            Log.e(TAG, "onCreate: user not passed");
        }

        Log.d(TAG, "onCreate: Received user " + user);

        // Utility to manage permission
        permViewUtil = new PermViewUtil(binding.getRoot());

        presenter = new ProfilePresenter(this);
        loggedInUserId = presenter.getLoggedInUserId();
        binding.setHandler(presenter);

        pictureUtil = new ProfilePictureUtil(presenter);

        setupCallList();
        setupMailList();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false);
        providerAdapter = new ProviderAdapter();
        providerAdapter.setChangeListener(curProvider -> {
            this.curProvider = curProvider;
            providerAdapter.unSelectExcept(curProvider);
            presenter.displayProviderDetails(curProvider);
        });

        binding.providerList.setLayoutManager(linearLayoutManager);
        binding.providerList.setAdapter(providerAdapter);

        binding.setHandler(presenter);

        binding.enterTag.setOnClickListener(v -> openTagDialog());

        return binding.getRoot();
    }

    private void openTagDialog() {
        new MaterialDialog.Builder(getContext())
                .title("Setting Tag")
                .content("Tag your friend")
                .inputType(InputType.TYPE_CLASS_TEXT)
                .input("Enter a tag", binding.enterTag.getText(), (dialog, input) -> {
                    if (!TextUtils.isEmpty(input.toString()))
                        presenter.saveTag(input.toString());
                })
                .show();
    }

    private void setupCallList() {
        LinearLayoutManager callListLayoutManager = new LinearLayoutManager(getContext());

        callListAdapter = new SingleViewAdapter<>(R.layout.call_item,
                (providerDetails) -> {
                    return new CallItemViewModel(permViewUtil, this, providerDetails,
                            loggedInUserId, presenter, user.uid.equals(loggedInUserId));
                });

        binding.callDetailList.setLayoutManager(callListLayoutManager);
        binding.callDetailList.setAdapter(callListAdapter);
    }

    private void setupMailList() {
        LinearLayoutManager mailListLayoutManager = new LinearLayoutManager(getContext());

        mailListAdapter = new SingleViewAdapter<>(R.layout.mail_item,
                item -> new MailItemViewModel(item, loggedInUserId, presenter,
                        user.uid.equals(loggedInUserId)));

        binding.emailDetailList.setLayoutManager(mailListLayoutManager);
        binding.emailDetailList.setAdapter(mailListAdapter);
    }

    public void showInputDialog() {
        new MaterialDialog.Builder(getContext())
                .title("Edit name")
                .inputType(InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_PERSON_NAME)
                .input(
                        "Enter name",
                        user.name,
                        (dialog, input) -> presenter.changeName(input.toString())
                )
                .show();
    }

    @Override
    public void displayProviderInfo(Provider provider, String providerDetail) {
        binding.setProvider(provider);
//        binding.providerName.setText(provider.name);
        binding.detailProvider.setText(providerDetail);
    }

    @Override
    public void showAccessButton(boolean enable) {
        if (enable) {
            binding.detailProvider.setVisibility(View.GONE);
            binding.requestAccess.setVisibility(View.VISIBLE);
        } else {
            binding.detailProvider.setVisibility(View.VISIBLE);
            binding.requestAccess.setVisibility(View.GONE);
        }
    }

    @Override
    public String getFriendTag() {
        return binding.enterTag.getText().toString();
    }

    @Override
    public void setFriendTag(String tag) {
        binding.enterTag.setVisibility(View.VISIBLE);

        if (TextUtils.isEmpty(tag) || tag.equals("true")) {
            binding.enterTag.setText("Click me to enter tag");
        } else {
            binding.enterTag.setText(tag);
        }
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
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;

            case R.id.action_change_picture:
                pictureUtil.openImageMenu();
                return true;

            case R.id.action_change_name:
                showInputDialog();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        pictureUtil.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case PermConstants.REQUEST_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pictureUtil.openCamera();
                } else {
                    displayError("Cannot save photo then");
                }
            }

            case PermConstants.REQUEST_WRITE_CONTACTS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // TODO: 21/1/17 this is wrong, check why the permission was requested
                    // BUG: it is toggling the contact when I changed my profile picture and
                    // permission was requested
                    presenter.toggleContact();
                } else {
                    displayError("Cannot save photo then");
                }
            }

            // other 'case' lines to check for other
            // permissions this app might request

            default:
                permViewUtil.onPermissionResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public Context getApplicationContext() {
        return getActivity().getApplicationContext();
    }

    @Override
    public void displayUser(User user) {
        displayProfilePic(user.pic);
        displayName(user.name);
    }

    @Override
    public void setAddFriendFabIcon(Boolean addFriendFabIcon) {
        if (addFriendFabIcon) {
            Log.d(TAG, "setAddFriendFabIcon: Setting text to save");
            binding.saveButton.setBackground(whiteRect);
            binding.saveButton.setText(R.string.save);
            binding.saveButton.setTextColor(colorAcc);
        }
        else {
            Log.d(TAG, "setAddFriendFabIcon: setting text to saved");
            binding.saveButton.setBackgroundColor(colorAcc);
            binding.saveButton.setTextColor(white);
            binding.saveButton.setText(R.string.saved);
        }
    }

    @Override
    public void addProvider(List<Provider> providerList) {
        if (providerList.size() > 0)
            curProvider = providerList.get(0);

        providerAdapter.setProviders(providerList);

        if (curProvider != null) {
            providerAdapter.unSelectExcept(curProvider);
            presenter.displayProviderDetails(curProvider);
        }
        else
            providerAdapter.unSelectAll();
    }

    @Override
    public void addCallDetails(ProviderDetails callDetails) {
        Log.d(TAG, "addCallDetails: Details to be added " + callDetails);
        callListAdapter.addItem(callDetails);
    }

    @Override
    public void addMailDetails(ProviderDetails mailDetails) {
        mailListAdapter.addItem(mailDetails);
    }

    @Override
    public void clearCallDetails() {
        callListAdapter.clear();
    }

    @Override
    public void clearMailDetails() {
        mailListAdapter.clear();
    }

    @Override
    public void launchSetup() {
//        Intent intent = new Intent(getContext(), ProviderPickerActivity.class);
//        startActivity(intent);
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, new EditProfileFragment());
        ft.addToBackStack(null);
        ft.commit();
    }

    public void displayError(String error) {
        Snackbar.make(binding.getRoot(), error, Snackbar.LENGTH_SHORT)
                .show();
    }

    @Override
    public void displayProfilePic(String url) {
        if (!TextUtils.isEmpty(url)) {
            Picasso.with(getContext())
                    .load(url)
                    .into(binding.profilePic);
        } else {
            Util.displayProfilePic(getContext(), binding.profilePic, user);
        }
    }

    @Override
    public void displayProfilePic(Bitmap image) {
        Util.resizeBitmapTo(image,
                binding.profilePic.getHeight(), binding.profilePic.getWidth())
                .compose(Util.applyComputationScheduler())
                .doOnNext(bitmap -> binding.profilePic.setImageBitmap(bitmap))
                .subscribe();
    }

    @Override
    public Fragment getFragment() {
        // TODO: 18/2/17 i think this is not required
        return this;
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
    public void displayName(String name) {
        binding.name.setText(name);
    }

    @Override
    public void loggedInUser(boolean show) {
        if (show) {
            Log.d(TAG, "loggedInUser: Setting text to edit profile");
            binding.saveButton.setText(R.string.edit_profile);
            binding.saveButton.setTextColor(colorAcc);
            binding.saveButton.setBackground(whiteRect);
            binding.enterTag.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void setProgressBar(boolean show) {
        if (show) {
            progressBar = new MaterialDialog.Builder(getContext())
                    .title("Please Wait")
                    .content("Updating profile")
                    .progress(true, 0)
                    .show();
        } else {
            progressBar.dismiss();
        }
    }

}
