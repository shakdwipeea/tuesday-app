package com.shakdwipeea.tuesday.profile;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.shakdwipeea.tuesday.R;
import com.shakdwipeea.tuesday.databinding.ActivityProfileBinding;
import com.shakdwipeea.tuesday.profile.edit.EditProfileFragment;
import com.shakdwipeea.tuesday.profile.view.ProfileViewFragment;

// ProfileActivity is independent from logged in user, so that the same can be used for
// viewing of other people profile
public class ProfileActivity extends AppCompatActivity {
    public static String TAG = "ProfileActivity";

    final public static String MODE_KEY = "modeKey";

    ActivityProfileBinding binding;

    public class ProfileActivityMode {
        final public static String EDIT_MODE = "edit";
        final public static String VIEW_MODE = "view";
    }

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

        String mode = getIntent().getStringExtra(MODE_KEY);

        if (mode == null) mode = ProfileActivityMode.VIEW_MODE;

        switch (mode) {
            case ProfileActivityMode.EDIT_MODE:
                loadFragment(new EditProfileFragment());
                break;
            case ProfileActivityMode.VIEW_MODE:
                loadFragment(new ProfileViewFragment());
                break;
            default:
                loadFragment(new ProfileViewFragment());
        }

    }

    public void loadFragment(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, fragment);
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
