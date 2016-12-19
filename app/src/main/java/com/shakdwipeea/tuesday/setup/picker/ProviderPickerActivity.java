package com.shakdwipeea.tuesday.setup.picker;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.shakdwipeea.tuesday.R;
import com.shakdwipeea.tuesday.databinding.ActivitySetupBinding;

public class ProviderPickerActivity extends AppCompatActivity implements PickerContract.View {
    ActivitySetupBinding binding;
    private PickerContract.Presenter presenter;

    @Override
    protected void onStart() {
        super.onStart();
        presenter.subscribe();
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.unsubscribe();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_setup);

        setSupportActionBar(binding.toolbar);

        presenter = new PickerPresenter(this);
        openPickerFragment();
    }

    public void openPickerFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.setup_fragment_container, new PickerFragment());
        ft.commit();
    }

    @Override
    public void displayError(String reason) {
        Snackbar.make(binding.getRoot(), reason, Snackbar.LENGTH_SHORT)
                .show();
    }

    @Override
    public void displayProgress(Boolean show) {

    }
}
