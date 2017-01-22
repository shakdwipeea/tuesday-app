package com.shakdwipeea.tuesday.profile;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.shakdwipeea.tuesday.R;
import com.shakdwipeea.tuesday.data.PermConstants;
import com.shakdwipeea.tuesday.data.entities.user.Provider;
import com.shakdwipeea.tuesday.data.entities.user.ProviderDetails;
import com.shakdwipeea.tuesday.data.entities.user.User;
import com.shakdwipeea.tuesday.databinding.ActivityProfileBinding;
import com.shakdwipeea.tuesday.databinding.MailItemBinding;
import com.shakdwipeea.tuesday.picture.ProfilePictureUtil;
import com.shakdwipeea.tuesday.setup.ProviderAdapter;
import com.shakdwipeea.tuesday.setup.picker.ProviderPickerActivity;
import com.shakdwipeea.tuesday.util.adapter.SingleViewAdapter;
import com.shakdwipeea.tuesday.util.Util;
import com.shakdwipeea.tuesday.util.perm.PermViewUtil;
import com.shakdwipeea.tuesday.util.perm.RequestPermissionInterface;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.util.List;

import rx.Observable;

// ProfileActivity is independent from logged in user, so that the same can be used for
// viewing of other people profile
public class ProfileActivity extends AppCompatActivity
        implements ProfileContract.View, RequestPermissionInterface {
    public static String TAG = "ProfileActivity";

    public static String PROFILE_IMAGE_EXTRA = "profilePic";
    public static String USER_EXTRA_KEY = "user";

    // we don't want to subscribe to presenter in case the intent has been launched
    // for changing the profile pic as the presenter downloads the high res profile pic
    //    private boolean profileChangeIntentLaunched;

    ActivityProfileBinding binding;

    private ProfilePresenter presenter;
    private Drawable thumbnailDrawable;

    MaterialDialog progressBar;

    User user;

    ProviderAdapter providerAdapter;
    SingleViewAdapter<ProviderDetails, CallItemViewModel> callListAdapter;
    SingleViewAdapter<ProviderDetails, MailItemViewModel> mailListAdapter;

    ProfilePictureUtil pictureUtil;

    PermViewUtil permViewUtil;

    int colorAcc;
    int white;

    Drawable whiteRect;

    @Override
    protected void onResume() {
        super.onResume();
        presenter.subscribe(user);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile);
        setSupportActionBar(binding.toolbar);

        colorAcc = ContextCompat.getColor(getContext(), R.color.colorAcc);
        white = ContextCompat.getColor(getContext(), android.R.color.white);
        whiteRect = ContextCompat.getDrawable(getContext(), R.drawable.rectangle_rounded);

        binding.toolbar.inflateMenu(R.menu.menu_profile);

        // Utility to manage permission
        permViewUtil = new PermViewUtil(binding.getRoot());

        presenter = new ProfilePresenter(this);
        binding.setHandler(presenter);

        pictureUtil = new ProfilePictureUtil(presenter, this);

        setSupportActionBar(binding.toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        user = Parcels.unwrap(getIntent().getParcelableExtra(USER_EXTRA_KEY));
        if (user == null) {
            displayError("User not provided");
            Log.e(TAG, "onCreate: user not passed");
            return;
        }

        Log.d(TAG, "onCreate: Received user " + user);

        setupCallList();
        setupMailList();

        // Info passed in Intent can be immediately opened and the rest be retrieved
        displayUser(user);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false);
        providerAdapter = new ProviderAdapter();
        providerAdapter.setChangeListener(curProvider -> {
            providerAdapter.unSelectExcept(curProvider);
            presenter.displayProviderDetails(curProvider);
        });

        binding.content.providerList.setLayoutManager(linearLayoutManager);
        binding.content.providerList.setAdapter(providerAdapter);

        binding.content.setHandler(presenter);
    }

    private void setupCallList() {
        LinearLayoutManager callListLayoutManager = new LinearLayoutManager(getContext());

        callListAdapter = new SingleViewAdapter<>(new CallItemViewModel(permViewUtil, this),
                R.layout.call_item);

        binding.content.callDetailList.setLayoutManager(callListLayoutManager);
        binding.content.callDetailList.setAdapter(callListAdapter);
    }

    private void setupMailList() {
        LinearLayoutManager mailListLayoutManager = new LinearLayoutManager(getContext());

        mailListAdapter = new SingleViewAdapter<>(new MailItemViewModel(),
                R.layout.mail_item);

        binding.content.emailDetailList.setLayoutManager(mailListLayoutManager);
        binding.content.emailDetailList.setAdapter(mailListAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
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

    public void showInputDialog() {
        new MaterialDialog.Builder(this)
                .title("Edit name")
                .content("Name")
                .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PERSON_NAME)
                .input(
                        "Enter name",
                        user.name,
                        (dialog, input) -> presenter.changeName(input.toString())
                )
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
    public void displayProviderInfo(Provider provider, String providerDetail) {
        binding.content.setProvider(provider);
        binding.content.providerName.setText(provider.name);
        binding.content.detailProvider.setText(providerDetail);
    }

    @Override
    public void showAccessButton(boolean enable) {
        if (enable) {
            binding.content.detailProvider.setVisibility(View.GONE);
            binding.content.requestAccess.setVisibility(View.VISIBLE);
        } else {
            binding.content.detailProvider.setVisibility(View.VISIBLE);
            binding.content.requestAccess.setVisibility(View.GONE);
        }
    }

    @Override
    public Context getContext() {
        return this;
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
            binding.content.saveButton.setBackground(whiteRect);
            binding.content.saveButton.setText(R.string.save);
            binding.content.saveButton.setTextColor(colorAcc);
        }
        else {
            Log.d(TAG, "setAddFriendFabIcon: setting text to saved");
            binding.content.saveButton.setBackgroundColor(colorAcc);
            binding.content.saveButton.setTextColor(white);
            binding.content.saveButton.setText(R.string.saved);
        }
    }

    @Override
    public void addProvider(List<Provider> providerList) {
        providerAdapter.setProviders(providerList);

        // show first provider
        presenter.displayProviderDetails(providerAdapter.getProvider(0));
        providerAdapter.unSelectExcept(providerAdapter.getProvider(0));
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
        Intent intent = new Intent(this, ProviderPickerActivity.class);
        startActivity(intent);
    }

    public void displayError(String error) {
        Snackbar.make(binding.getRoot(), error, Snackbar.LENGTH_SHORT)
                .show();
    }

    @Override
    public void displayProfilePic(String url) {
        if (!TextUtils.isEmpty(url)) {
            Picasso.with(this)
                    .load(url)
                    .into(binding.content.profilePic);
        } else {
            Util.displayProfilePic(getContext(), binding.content.profilePic,
                    binding.content.placeholderProfilePic, user);
        }
    }

    @Override
    public void displayProfilePic(Bitmap image) {
        Util.resizeBitmapTo(image, binding.content.profilePic.getHeight(), binding.content.profilePic.getWidth())
                .compose(Util.applyComputationScheduler())
                .doOnNext(bitmap -> binding.content.profilePic.setImageBitmap(bitmap))
                .subscribe();
    }

    @Override
    public void displayProfilePicFromPath(String photoPath) {
        Util.resizeBitmapTo(photoPath,
                binding.content.profilePic.getHeight(), binding.content.profilePic.getWidth())
                .compose(Util.applyComputationScheduler())
                .doOnNext(bitmap -> binding.content.profilePic.setImageBitmap(bitmap))
                .doOnError(throwable -> displayError(throwable.getMessage()))
                .onErrorResumeNext(Observable.empty())
                .subscribe();
    }

    @Override
    public void displayName(String name) {
        binding.content.name.setText(name);
    }

    @Override
    public void loggedInUser(boolean show) {
        if (show) {
            Log.d(TAG, "loggedInUser: Setting text to edit profile");
            binding.content.saveButton.setText(R.string.edit_profile);
            binding.content.saveButton.setTextColor(colorAcc);
            binding.content.saveButton.setBackground(whiteRect);
        } else {
            binding.toolbar.getMenu().clear();
        }
    }

    @Override
    public void setProgressBar(boolean show) {
        if (show) {
            progressBar = new MaterialDialog.Builder(this)
                    .title("Please Wait")
                    .content("Updating profile")
                    .progress(true, 0)
                    .show();
        } else {
            progressBar.dismiss();
        }
    }

    /**
     * Request permission at run time
     *
     * @param permissions    List of permission to be requested
     * @param permIdentifier Identifier used to map the requests
     */
    @Override
    public void requestPermission(String[] permissions, int permIdentifier) {
        ActivityCompat.requestPermissions(this, permissions, permIdentifier);
    }
}
