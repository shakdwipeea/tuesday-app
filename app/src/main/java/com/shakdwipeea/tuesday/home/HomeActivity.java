package com.shakdwipeea.tuesday.home;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;

import com.jakewharton.rxbinding.widget.RxTextView;
import com.shakdwipeea.tuesday.R;
import com.shakdwipeea.tuesday.databinding.ActivityHomeBinding;

import java.util.concurrent.TimeUnit;

import rx.Subscription;

public class HomeActivity extends AppCompatActivity implements HomeContract.View {
    private static final String TAG = "HomeActivity";

    ActivityHomeBinding binding;
    Context context;

    HomePresenter presenter;

    Subscription subscription;

    ContactAdapter adapter;

    @Override
    protected void onResume() {
        super.onResume();
        presenter.subscribe(context);
        setupSearch();
    }

    @Override
    protected void onPause() {
        super.onPause();
        presenter.unsubscribe();
        subscription.unsubscribe();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home);

        context = this;

        setupTabs();

        adapter = new ContactAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.contactList.setLayoutManager(layoutManager);
        binding.contactList.setAdapter(adapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(context,
                layoutManager.getOrientation());
        binding.contactList.addItemDecoration(dividerItemDecoration);

        presenter = new HomePresenter(this);
    }

    private void setupSearch() {
        subscription = RxTextView.textChanges(binding.search)
                .filter(charSequence -> charSequence.length() > 2)
                .debounce(100, TimeUnit.MILLISECONDS)
                .switchMap(charSequence -> presenter.searchName(charSequence.toString()))
                .subscribe(
                        users -> {
                            adapter.setUsers(users);
                            adapter.notifyDataSetChanged();
                        },
                        Throwable::printStackTrace
                );
    }

    private void setupTabs() {
        int tabIconWhiteColor = ContextCompat.getColor(context, R.color.tw__solid_white);
        int tabIconAccentColor = ContextCompat.getColor(context, R.color.colorAccent);

        // Add white filter to all tab icons
        int numTabs = binding.homeTab.getTabCount();
        for (int i = 0; i < numTabs; i++) {
            TabLayout.Tab tab = binding.homeTab.getTabAt(i);

            if (tab != null) {
                Drawable tabIcon = tab.getIcon();
                if (tabIcon != null) {
                    if (tab.isSelected())
                        tabIcon.setColorFilter(tabIconAccentColor, PorterDuff.Mode.SRC_IN);
                    else
                        tabIcon.setColorFilter(tabIconWhiteColor, PorterDuff.Mode.SRC_IN);
                }
            }
        }

        // Make selected tab as accent color
        binding.homeTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Drawable tabIcon = tab.getIcon();
                if (tabIcon != null) {
                    tabIcon.setColorFilter(tabIconAccentColor, PorterDuff.Mode.SRC_IN);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                Drawable tabIcon = tab.getIcon();
                if (tabIcon != null) {
                    tabIcon.setColorFilter(tabIconWhiteColor, PorterDuff.Mode.SRC_IN);
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public void displayError(String message) {
        Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_SHORT)
                .show();
    }
}
