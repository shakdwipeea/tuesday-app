package com.shakdwipeea.tuesday.home;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.shakdwipeea.tuesday.home.home.HomeFragment;
import com.shakdwipeea.tuesday.home.notification.NotificationFragment;
import com.shakdwipeea.tuesday.home.settings.SettingsFragment;

/**
 * Created by ashak on 10-12-2016.
 */

public class HomePagerAdapter extends FragmentPagerAdapter {
    private HomeFragment homeFragment = new HomeFragment();
    private NotificationFragment notificationFragment = new NotificationFragment();
    private SettingsFragment settingsFragment = new SettingsFragment();

    public HomePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
//        Drawable tabIcon = tab.getIcon();
//        if (tabIcon != null) {
//            tabIcon.setColorFilter(tabIconAccentColor, PorterDuff.Mode.SRC_IN);
//        }

        switch (position) {
            case 0:
                return homeFragment;
            case 1:
                return notificationFragment;
            case 2:
                return settingsFragment;
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }


}
