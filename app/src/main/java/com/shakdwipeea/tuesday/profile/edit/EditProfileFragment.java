package com.shakdwipeea.tuesday.profile.edit;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.afollestad.materialdialogs.MaterialDialog;
import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.login.LoginResult;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.shakdwipeea.tuesday.R;
import com.shakdwipeea.tuesday.data.Preferences;
import com.shakdwipeea.tuesday.data.entities.user.Provider;
import com.shakdwipeea.tuesday.data.entities.user.User;
import com.shakdwipeea.tuesday.databinding.FragmentEditProfileBinding;
import com.shakdwipeea.tuesday.picture.ProfilePicturePresenter;
import com.shakdwipeea.tuesday.picture.ProfilePictureUtil;
import com.shakdwipeea.tuesday.picture.ProfilePictureView;
import com.shakdwipeea.tuesday.util.Util;
import com.shakdwipeea.tuesday.util.perm.PermViewUtil;
import com.shakdwipeea.tuesday.util.perm.RequestPermissionInterface;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.util.concurrent.TimeUnit;

import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;
import rx.Observable;
import rx.Subscription;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditProfileFragment extends Fragment
        implements EditProfileContract.View, ProfilePictureView, RequestPermissionInterface {
    private static final String TAG = "EditProfileFragment";

    FragmentEditProfileBinding binding;

    EditProfileAdapter providerAdapter;

    EditProfilePresenter editProfilePresenter;

    ProfilePictureUtil profilePictureUtil;

    MaterialDialog progressDialog;

    Subscription subscription;

    CallbackManager fbCallbackManager;

    EditProfileContract.SaveImportData saveImportData;
    private User user;

    public EditProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FacebookSdk.sdkInitialize(getApplicationContext());

        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_edit_profile,
                container, false);

        setHasOptionsMenu(true);

        editProfilePresenter = new EditProfilePresenter(this);

        setupNameDisplay();

        profilePictureUtil = new ProfilePictureUtil(new ProfilePicturePresenter(this));
        binding.cameraIcon.setOnClickListener(v -> {
            PermViewUtil permViewUtil = new PermViewUtil(binding.getRoot());
            permViewUtil.performActionWithPermissions(
                    getContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA
                    },
                    this,
                    () -> profilePictureUtil.openImageMenu()
            );
        });

        progressDialog = Util.createProgressDialog(getContext()).build();

        setupRecyclerViews();

        fbCallbackManager = CallbackManager.Factory.create();
        setupFbLogin();

        return binding.getRoot();
    }

    private void setupFbLogin() {
        binding.fbLoginButton.setReadPermissions("email");
        binding.fbLoginButton.setFragment(this);

        binding.fbLoginButton.registerCallback(fbCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Bundle params = new Bundle();
                        params.putString("fields", "email");

                        GraphRequest graphRequest = GraphRequest
                                .newMeRequest(loginResult.getAccessToken(),
                                        (object, response) -> {
                                            try {
                                                String email = object.getString("email");

                                                if (saveImportData != null)
                                                    saveImportData.save(email);

                                                Log.d(TAG, "onSuccess: Email is " + email);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        });

                        graphRequest.setParameters(params);
                        graphRequest.executeAsync();
                    }

                    @Override
                    public void onCancel() {
                        displayError("Login cancelled");
                    }

                    @Override
                    public void onError(FacebookException error) {
                        displayError(error.getMessage());
                        error.printStackTrace();
                    }
                });
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
        this.user = user;
        binding.nameInput.setText(user.name);

        if (user.pic != null) {
            Picasso.with(getContext())
                    .load(user.pic)
                    .into(binding.profilePic);
        }
    }

    public void setupRecyclerViews() {
        LinearLayoutManager providerLM = new LinearLayoutManager(getContext());
        providerAdapter = new EditProfileAdapter(getContext(), editProfilePresenter,
                saveImportData -> {
                    this.saveImportData = saveImportData;
                    binding.fbLoginButton.performClick();
                });

        DividerItemDecoration decoration = new DividerItemDecoration(getContext(),
                providerLM.getOrientation());

        binding.providerList.setLayoutManager(providerLM);
        binding.providerList.setAdapter(providerAdapter);
        binding.providerList.setItemAnimator(new SlideInUpAnimator());
        binding.providerList.addItemDecoration(decoration);
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
                try {
                    InputMethodManager keyboard = (InputMethodManager) getContext()
                            .getSystemService(Context.INPUT_METHOD_SERVICE);

                    if (getView() != null)
                        keyboard.hideSoftInputFromInputMethod(getView().getWindowToken(), 0);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    getActivity().onBackPressed();
                }
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
        fbCallbackManager.onActivityResult(requestCode, resultCode, data);
        profilePictureUtil.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void displayProfilePic(String url) {
        if (!TextUtils.isEmpty(url)) {
            Picasso.with(getContext())
                    .load(url)
                    .into(binding.profilePic);
        } else {
            binding.profilePic.setImageDrawable(
                    TextDrawable.builder()
                            .buildRound(
                                    String.valueOf(user.name.toUpperCase().charAt(0)),
                                    ColorGenerator.MATERIAL.getColor(user.name))
            );
        }
    }

    /**
     * Callback for the result from requesting permissions. This method
     * is invoked for every call on {@link #requestPermissions(String[], int)}.
     * <p>
     * <strong>Note:</strong> It is possible that the permissions request interaction
     * with the user is interrupted. In this case you will receive empty permissions
     * and results arrays which should be treated as a cancellation.
     * </p>
     *
     * @param requestCode  The request code passed in {@link #requestPermissions(String[], int)}.
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either {@link PackageManager#PERMISSION_GRANTED}
     *                     or {@link PackageManager#PERMISSION_DENIED}. Never null.
     * @see #requestPermissions(String[], int)
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        // Any correct permission go to camera
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            profilePictureUtil.openCamera();

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
    public Fragment getFragment() {
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
    public void addProvider(Provider provider) {
        providerAdapter.addProvider(provider);
    }

    @Override
    public void clearProvider() {
        providerAdapter.clear();
    }
}
