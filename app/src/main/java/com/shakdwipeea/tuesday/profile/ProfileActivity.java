package com.shakdwipeea.tuesday.profile;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;

import com.shakdwipeea.tuesday.R;
import com.shakdwipeea.tuesday.databinding.ActivityProfileBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {
    public static String PROFILE_IMAGE_EXTRA = "profilePic";

    private ContactAdapter arrayAdapter;

    ActivityProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile);
        setSupportActionBar(binding.toolbar);

        String profilePic = getIntent().getStringExtra(PROFILE_IMAGE_EXTRA);

        if (profilePic == null) {
            displayError("Profile pic not provided");
        } else {
            Picasso.with(this)
                    .load(profilePic).into(binding.profilePic);
        }

        binding.fab.setOnClickListener(view ->
                Snackbar
                        .make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null)
                        .show());

        List<String> items = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            items.add("I am item " + i);
        }

        arrayAdapter =new ContactAdapter(items, this);

        binding.profileToolbarContainer.scrollableview.setLayoutManager(new LinearLayoutManager(this));
        binding.profileToolbarContainer.scrollableview.setAdapter(arrayAdapter);
    }

    private void displayError(String error) {
        Snackbar.make(binding.getRoot(), error, Snackbar.LENGTH_SHORT)
                .show();
    }
}
