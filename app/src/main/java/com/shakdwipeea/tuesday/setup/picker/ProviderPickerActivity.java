package com.shakdwipeea.tuesday.setup.picker;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.shakdwipeea.tuesday.R;
import com.shakdwipeea.tuesday.databinding.ActivitySetupBinding;

public class ProviderPickerActivity extends AppCompatActivity {
    ActivitySetupBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_setup);

        setSupportActionBar(binding.toolbar);

        openPickerFragment();
    }

    public void openPickerFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.setup_fragment_container, new PickerFragment());
        ft.commit();
    }

}
