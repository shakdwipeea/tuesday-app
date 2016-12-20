package com.shakdwipeea.tuesday.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.shakdwipeea.tuesday.data.entities.user.User;

/**
 * Created by ashak on 15-10-2016.
 */

public class Preferences {
    private static String SHARED_PREFS_FILE_NAME = "com.shakdwipeea.tuesday.preferences";

    private static String KEY_NAME_INDEXED = "name_indexed";
    private static String KEY_SETUP_DONE = "setup_complete";

    private static String KEY_USER_NAME = "user";
    private static String KEY_USER_PHOTO = "photo";

    private static Preferences preferences;

    private SharedPreferences sharedPreferences;

    private Preferences(Context context) {
        sharedPreferences= context
                .getSharedPreferences(Preferences.SHARED_PREFS_FILE_NAME, Context.MODE_PRIVATE);
    }

    // TODO: 07-11-2016 clear preferences at log out
    public static Preferences getInstance(Context context) {
        if (preferences == null) preferences = new Preferences(context);

        return preferences;
    }

    public boolean isNameIndexed() {
        return sharedPreferences.getBoolean(KEY_NAME_INDEXED, false);
    }

    public void setNameIndexed(boolean value) {
        sharedPreferences.edit()
                .putBoolean(KEY_NAME_INDEXED, value)
                .apply();
    }

    public boolean isSetupComplete() {
        return sharedPreferences.getBoolean(KEY_SETUP_DONE, false);
    }

    public void setSetupComplete(boolean value) {
        sharedPreferences.edit()
                .putBoolean(KEY_SETUP_DONE, value)
                .apply();
    }

    public void setUserDetails(User user) {
        sharedPreferences.edit()
                .putString(KEY_USER_NAME, user.name)
                .putString(KEY_USER_PHOTO, user.pic)
                .apply();
    }

    public void clearUserDetails() {
        sharedPreferences.edit()
                .remove(KEY_USER_NAME)
                .remove(KEY_USER_PHOTO)
                .apply();
    }

    public User getUserDetails() {
        User user = new User();
        user.name = sharedPreferences.getString(KEY_USER_NAME, "default");
        user.pic = sharedPreferences.getString(KEY_USER_PHOTO, null);
        return user;
    }

    public void clear() {
        setNameIndexed(false);
        setSetupComplete(false);
        clearUserDetails();
    }
}
