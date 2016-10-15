package com.shakdwipeea.tuesday.profile;

import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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

public class ProfileActivity extends AppCompatActivity implements ProfileContract.View {
    public static String TAG = "ProfileActivity";

    public static String PROFILE_IMAGE_EXTRA = "profilePic";

    private ContactAdapter arrayAdapter;

    ActivityProfileBinding binding;

    private ProfileContract.Presenter presenter;
    private Drawable thumbnailDrawable;

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

        // display the low res profile pic initially
        String profilePic = getIntent().getStringExtra(PROFILE_IMAGE_EXTRA);
        Log.d(TAG, "Profile url " + profilePic);

        if (profilePic == null) {
            displayError("Profile pic not provided");
        } else {
            getLowResDrawable(profilePic);
        }

        List<String> items = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            items.add("I am item " + i);
        }

        arrayAdapter =new ContactAdapter(items, this);

        binding.profileToolbarContainer.scrollableview
                .setLayoutManager(new LinearLayoutManager(this));
        binding.profileToolbarContainer.scrollableview.setAdapter(arrayAdapter);
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

}
