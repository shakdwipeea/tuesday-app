package com.shakdwipeea.tuesday.auth;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.shakdwipeea.tuesday.R;
import com.shakdwipeea.tuesday.auth.phone.PhoneInputFragment;
import com.shakdwipeea.tuesday.databinding.ActivityAuthBinding;

public class AuthActivity extends AppCompatActivity {
    private static final String TAG = "AuthActivity";

    private ActivityAuthBinding binding;

    @Override
    public void
    onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_auth);

        loadFragment(new PhoneInputFragment());

    }

    public void loadFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.auth_fragment_container, fragment);
        fragmentTransaction.commit();
    }
}
