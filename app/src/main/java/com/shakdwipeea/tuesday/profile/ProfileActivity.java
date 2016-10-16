package com.shakdwipeea.tuesday.profile;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;

import com.shakdwipeea.tuesday.R;
import com.shakdwipeea.tuesday.databinding.ActivityProfileBinding;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity
        implements ProfileContract.View, ProfileContract.IntentActions {
    public static String TAG = "ProfileActivity";

    public static String PROFILE_IMAGE_EXTRA = "profilePic";

    private static final int PHOTO_PICKER_REQUEST_CODE = 260;
    private static final int REQUEST_IMAGE_CAPTURE = 582;

    private ContactAdapter arrayAdapter;

    ActivityProfileBinding binding;

    private ProfileContract.Presenter presenter;
    private Drawable thumbnailDrawable;

    private BottomSheetBehavior bottomSheetBehavior;

    @Override
    protected void onResume() {
        super.onResume();
        presenter.subscribe();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile);
        setSupportActionBar(binding.toolbar);

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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
                .into(binding.profilePic);
    }

    @Override
    public void displayName(String name) {
        binding.toolbar.setTitle(name);
    }

    @Override
    public void openPhotoPicker() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, PHOTO_PICKER_REQUEST_CODE);
    }

    @Override
    public void openCamera() {
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
