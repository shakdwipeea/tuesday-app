package com.shakdwipeea.tuesday.home;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.shakdwipeea.tuesday.R;
import com.shakdwipeea.tuesday.databinding.ActivityHomeBinding;
import com.shakdwipeea.tuesday.home.home.HomeFragment;
import com.shakdwipeea.tuesday.home.notification.NotificationFragment;
import com.shakdwipeea.tuesday.home.settings.SettingsFragment;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "HomeActivity";

    ActivityHomeBinding binding;

    Context context;

    private HomeFragment homeFragment;
    private NotificationFragment notificationFragment;
    private SettingsFragment settingsFragment;

    private HomePagerAdapter pagerAdapter;

    private OnBackPressedListener backPressedListener;

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home);

        context = this;

        pagerAdapter = new HomePagerAdapter(getSupportFragmentManager());
        binding.homeTab.setupWithViewPager(binding.homeFragmentContainer);

        binding.homeFragmentContainer.setAdapter(pagerAdapter);
        binding.homeFragmentContainer.setOffscreenPageLimit(3);
        binding.homeFragmentContainer.setCurrentItem(0);

        setupTabs();

        //loadFragment(homeFragment);
    }

    public void setBackPressedListener(OnBackPressedListener backPressedListener) {
        this.backPressedListener = backPressedListener;
    }

    private void setupTabs() {
        int tabIconWhiteColor = ContextCompat.getColor(context, R.color.tw__solid_white);
        int tabIconAccentColor = ContextCompat.getColor(context, R.color.black);

        // Add white filter to all tab icons
        int numTabs = binding.homeTab.getTabCount();
        for (int i = 0; i < numTabs; i++) {
            TabLayout.Tab tab = binding.homeTab.getTabAt(i);

            if (tab != null) {
                Drawable tabIcon = tab.getIcon();
                if (tabIcon == null) {
                    switch (i) {
                        case 0:
                            tabIcon = ContextCompat.getDrawable(this,
                                    R.drawable.ic_home);

                            break;
                        case 1:
                            tabIcon = ContextCompat.getDrawable(this,
                                    R.drawable.ic_notifications_none_black_24dp);
                            break;
                        case 2:
                            tabIcon = ContextCompat.getDrawable(this,
                                    R.drawable.ic_icon_filters);
                            break;
                        default:
                            tabIcon = ContextCompat.getDrawable(this,
                                    R.drawable.ic_person_add_black_24dp);
                    }
                    tabIcon.setColorFilter(tabIconAccentColor, PorterDuff.Mode.SRC_IN);
                    tab.setIcon(tabIcon);
                }

                setTabIcon(tab, false);
            }
        }

        setTabIcon(binding.homeTab.getTabAt(0), true);

        // Make selected tab as accent color
        binding.homeTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Fragment item = pagerAdapter.getItem(tab.getPosition());
                if (item instanceof FragmentViewPagerLifeCycle) {
                    ((FragmentViewPagerLifeCycle) item).onResumeFragment();
                }

                setTabIcon(tab, true);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                Fragment item = pagerAdapter.getItem(tab.getPosition());
                if (item instanceof FragmentViewPagerLifeCycle) {
                    ((FragmentViewPagerLifeCycle) item).onPauseFragment();
                }

                setTabIcon(tab, false);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                setTabIcon(tab, true);
            }

        });
    }

    private void setTabIcon(TabLayout.Tab tab, boolean selectd) {
        int position = tab.getPosition();

        switch (position) {
            case 0:
                if (selectd)
                    tab.setIcon(getDrawableFromId(R.drawable.ic_home_black_24dp));
                else
                    tab.setIcon(getDrawableFromId(R.drawable.ic_home));
                break;

            case 1:
                if (selectd)
                    tab.setIcon(getDrawableFromId(R.drawable.ic_notifications_black_24dp));
                else
                    tab.setIcon(getDrawableFromId(R.drawable.ic_notification));
                break;

            case 2:
                if (selectd)
                    tab.setIcon(getDrawableFromId(R.drawable.ic_tune_black_24dp));
                else
                    tab.setIcon(getDrawableFromId(R.drawable.ic_icon_filters));
                break;
        }
    }

    private Drawable getDrawableFromId(int id) {
        return ContextCompat.getDrawable(context, id);
    }

    @Override
    public void onBackPressed() {
        if (backPressedListener == null || backPressedListener.doBack()) {
            super.onBackPressed();
        }
    }

    private void displayError(String reason) {
        Snackbar.make(binding.getRoot(), reason, Snackbar.LENGTH_SHORT)
                .show();
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.home_fragment_container, fragment);
        ft.commit();
    }
}
