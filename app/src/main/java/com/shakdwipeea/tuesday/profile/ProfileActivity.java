package com.shakdwipeea.tuesday.profile;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.shakdwipeea.tuesday.R;
import com.shakdwipeea.tuesday.databinding.ActivityProfileBinding;
import com.shakdwipeea.tuesday.profile.view.ProfileViewFragment;

import org.parceler.Parcels;

// ProfileActivity is independent from logged in user, so that the same can be used for
// viewing of other people profile
public class ProfileActivity extends AppCompatActivity {
    public static String TAG = "ProfileActivity";

    ActivityProfileBinding binding;

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile);
        setSupportActionBar(binding.toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        loadFragment();
    }

    public void loadFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, new ProfileViewFragment());
        ft.commit();
    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case android.R.id.home:
//                onBackPressed();
//                return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

}
