package com.shakdwipeea.tuesday.profile;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.shakdwipeea.tuesday.R;
import com.shakdwipeea.tuesday.auth.AuthActivity;
import com.shakdwipeea.tuesday.databinding.ActivityProfileBinding;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity
        implements ProfileContract.View, ProfileContract.IntentActions {
    public static String TAG = "ProfileActivity";

    public static String PROFILE_IMAGE_EXTRA = "profilePic";

    private static final int PHOTO_PICKER_REQUEST_CODE = 260;
    private static final int REQUEST_IMAGE_CAPTURE = 582;

    private boolean profileChangeIntentLaunched;

    private ContactAdapter arrayAdapter;

    ActivityProfileBinding binding;

    private ProfileContract.Presenter presenter;
    private Drawable thumbnailDrawable;

    private BottomSheetBehavior bottomSheetBehavior;

    @Override
    protected void onResume() {
        super.onResume();

        // we don't want to subscribe to presenter in case the intent has been launched
        // for changing the profile pic as the presenter downloads the high res profile pic
        if (!profileChangeIntentLaunched) presenter.subscribe();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile);
        setSupportActionBar(binding.toolbar);

        binding.toolbar.inflateMenu(R.menu.menu_profile);

        profileChangeIntentLaunched = false;

        presenter = new ProfilePresenter(this);
        binding.setHandler(presenter);
        binding.setIntentHandler(this);

        // display the low res profile pic initially
        String profilePic = getIntent().getStringExtra(PROFILE_IMAGE_EXTRA);
        Log.d(TAG, "Profile url " + profilePic);

        if (profilePic == null) {
            displayError("Profile pic not provided");
        } else {
            getLowResDrawable(profilePic);
        }

        bottomSheetBehavior = BottomSheetBehavior.from(binding.editPicBottomSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        List<String> items = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            items.add("I am item " + i);
        }

        arrayAdapter =new ContactAdapter(items, this);

        binding.profileToolbarContainer.scrollableview
                .setLayoutManager(new LinearLayoutManager(this));
        binding.profileToolbarContainer.scrollableview.setAdapter(arrayAdapter);
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
            case R.id.action_change_picture:
                openImageMenu();
                return true;

            case R.id.action_logout:
                presenter.logout();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        switch (requestCode) {
            case PHOTO_PICKER_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    try {
                        final Uri imageUri = data.getData();
                        final InputStream imageStream = getContentResolver()
                                .openInputStream(imageUri);
                        presenter.updateProfilePic(imageStream);
                    } catch (FileNotFoundException e) {
                        displayError(e.getMessage());
                        e.printStackTrace();
                    }
                }
        }
    }

    @Override
    public void launchAuth() {
        Intent intent = new Intent(this, AuthActivity.class);
        startActivity(intent);
        finish();
    }

    public void displayError(String error) {
        Snackbar.make(binding.getRoot(), error, Snackbar.LENGTH_SHORT)
                .show();
    }

    // a hack to get the thumbnail as placeholder
    // see https://github.com/square/picasso/issues/383
    private void getLowResDrawable(String profilePic) {
        Picasso.with(this)
                .load(profilePic)
                .into(binding.profilePic, new Callback() {
                    @Override
                    public void onSuccess() {
                        thumbnailDrawable = binding.profilePic.getDrawable();
                    }

                    @Override
                    public void onError() {
                        Log.e(TAG, "onError: No thumbnail");
                    }
                });
    }

    @Override
    public void displayProfilePic(String url) {
        Picasso.with(this)
                .load(url)
                .placeholder(thumbnailDrawable)
                .resize(500, 0)
                .into(binding.profilePic);
    }

    @Override
    public void displayProfilePic(Bitmap image) {
        binding.profilePic.setImageBitmap(image);
    }

    @Override
    public void displayName(String name) {
        binding.toolbar.setTitle(name);
    }

    @Override
    public void openPhotoPicker() {
        profileChangeIntentLaunched = true;
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, PHOTO_PICKER_REQUEST_CODE);
    }

    @Override
    public void openCamera() {
        profileChangeIntentLaunched = true;
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void openImageMenu() {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

}
