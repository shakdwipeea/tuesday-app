package com.shakdwipeea.tuesday.home;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.shakdwipeea.tuesday.R;
import com.shakdwipeea.tuesday.databinding.ActivityHomeBinding;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "HomeActivity";

    ActivityHomeBinding binding;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home);

        context = this;

        setupTabs();
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
}
